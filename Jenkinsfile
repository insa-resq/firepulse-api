pipeline {
    agent any

    parameters {
        choice(
            name: 'SERVICE',
            choices: [
                'ALL',
                'config-service',
                'discovery-service',
                'brigadeflow-service',
                'pyrosense-service',
                'stationlogix-service'
            ],
            description: 'Select service to deploy'
        )
    }

    environment {
        DEPLOYMENT_SERVER = '192.168.10.46'
        DEPLOY_PATH = '~/firepulse-api'
        IMAGE_TAG = 'latest'

        DEPLOYMENT_USER = credentials('deployment-server-ssh')
        GITHUB_TOKEN = credentials('github-token')

        DATABASE_HOST = '192.168.20.150'
        DATABASE_NAME = 'firepulse_db'
        DATABASE_USER = credentials('database-user')
        DATABASE_PASSWORD = credentials('database-password')
    }

    stages {
        stage('Determine Services to Deploy') {
            steps {
                script {
                    // Check if triggered by GitHub Actions via parameters
                    if (params.SERVICE && params.SERVICE != 'ALL') {
                        env.SERVICES_TO_DEPLOY = params.SERVICE
                        echo "Deployment triggered for service: ${params.SERVICE}"
                    } else if (params.SERVICE == 'ALL') {
                        env.SERVICES_TO_DEPLOY = 'config-service,discovery-service,brigadeflow-service,pyrosense-service,stationlogix-service'
                        echo "Manual deployment of all services"
                    } else {
                        env.SERVICES_TO_DEPLOY = 'config-service,discovery-service,brigadeflow-service,pyrosense-service,stationlogix-service'
                        echo "Default: deploying all services"
                    }

                    // Calculate deployment order respecting dependencies
                    def services = env.SERVICES_TO_DEPLOY.split(',')
                    def orderedServices = calculateDeploymentOrder(services)
                    env.ORDERED_SERVICES = orderedServices.join(',')
                    echo "Deployment order: ${env.ORDERED_SERVICES}"
                }
            }
        }

        stage('Transfer Configuration') {
            steps {
                script {
                    sshagent(credentials: ['deployment-server-ssh']) {
                        // Create/Override .env file on deployment server
                        sh """
                            ssh -o StrictHostKeyChecking=no ${DEPLOYMENT_USER}@${DEPLOYMENT_SERVER} '
                                mkdir -p ${DEPLOY_PATH}
                                echo "DATABASE_HOST=${DATABASE_HOST}" > ${DEPLOY_PATH}/.env
                                echo "DATABASE_NAME=${DATABASE_NAME}" >> ${DEPLOY_PATH}/.env
                                echo "DATABASE_USER=${DATABASE_USER}" >> ${DEPLOY_PATH}/.env
                                echo "DATABASE_PASSWORD=${DATABASE_PASSWORD}" >> ${DEPLOY_PATH}/.env
                                chmod 600 ${DEPLOY_PATH}/.env
                            '
                        """

                        // Transfer docker-compose.yaml
                        sh """
                            scp -o StrictHostKeyChecking=no docker-compose.yaml ${DEPLOYMENT_USER}@${DEPLOYMENT_SERVER}:${DEPLOY_PATH}/
                        """
                    }
                }
            }
        }

        stage('Deploy Services') {
            steps {
                script {
                    def services = env.ORDERED_SERVICES.split(',')

                    sshagent(credentials: ['deployment-server-ssh']) {

                        sh """
                            ssh -o StrictHostKeyChecking=no ${DEPLOYMENT_USER}@${DEPLOYMENT_SERVER} '
                                echo "${GITHUB_TOKEN}" | docker login ghcr.io -u "jenkins" --password-stdin
                            '
                        """

                        try {
                            services.each { service ->
                                stage("Deploy ${service}") {
                                    echo "Deploying ${service}..."
                                    try {
                                        // Execute on remote, capture exit status
                                        def result = sh(
                                            script: """
                                                ssh -o StrictHostKeyChecking=no ${DEPLOYMENT_USER}@${DEPLOYMENT_SERVER} '
                                                    set -e

                                                    cd ${DEPLOY_PATH}

                                                    echo "--- Pulling updated image for ${service} ---"
                                                    export IMAGE_TAG=${IMAGE_TAG}
                                                    docker compose pull ${service}

                                                    echo "--- Starting ${service} ---"
                                                    docker compose up -d ${service} --wait

                                                    echo "--- ${service} deployed successfully ---"
                                                '
                                            """,
                                            returnStatus: true
                                        )

                                        if (result != 0) {
                                            error("Deployment of ${service} failed with exit code ${result}")
                                        }
                                    } catch (err) {
                                        echo "Exception while deploying ${service}: ${err}"
                                        error("Deployment of ${service} failed")
                                    }
                                }
                            }
                        } catch (err) {
                            echo "Deployment process encountered an error: ${err}"
                            error(err.toString())
                        } finally {
                            // always attempt to clean up images and logout from registry
                            sh """
                                ssh -o StrictHostKeyChecking=no ${DEPLOYMENT_USER}@${DEPLOYMENT_SERVER} '
                                    docker image prune -f
                                    docker logout ghcr.io
                                '
                            """
                        }
                    }
                }
            }
        }
    }

    post {
        success {
            echo "✓ Deployment completed successfully!"
            script {
                def services = env.ORDERED_SERVICES.split(',')
                echo "Deployed services: ${services.join(', ')}"
            }
        }
        failure {
            echo "✗ Deployment failed!"
            script {
                def services = env.ORDERED_SERVICES.split(',')
                echo "Services attempted: ${services.join(', ')}"
            }
        }
        always {
            cleanWs()
        }
    }
}

// Helper function to calculate deployment order based on dependencies
def calculateDeploymentOrder(services) {
    def depMap = [
        'config-service': [],
        'discovery-service': ['config-service'],
        'brigadeflow-service': ['config-service', 'discovery-service'],
        'pyrosense-service': ['config-service', 'discovery-service'],
        'stationlogix-service': ['config-service', 'discovery-service']
    ]

    def allServices = services as List
    def ordered = []

    // Add dependencies of selected services
    def servicesToInclude = allServices.clone()
    for (service in allServices) {
        if (depMap.containsKey(service)) {
            servicesToInclude.addAll(depMap[service])
        }
    }
    servicesToInclude = servicesToInclude.unique()

    // Sort by dependency order
    def maxIterations = 10
    def iteration = 0

    while (!servicesToInclude.isEmpty() && iteration < maxIterations) {
        iteration++
        def added = false

        for (service in servicesToInclude.clone()) {
            def deps = depMap[service] ?: []
            def depsInServices = deps.findAll { servicesToInclude.contains(it) }

            if (depsInServices.isEmpty() || depsInServices.every { ordered.contains(it) }) {
                ordered.add(service)
                servicesToInclude.remove(service)
                added = true
            }
        }

        if (!added && !servicesToInclude.isEmpty()) {
            // Circular dependency or missing service - add remaining in order
            ordered.addAll(servicesToInclude)
            break
        }
    }

    return ordered
}
