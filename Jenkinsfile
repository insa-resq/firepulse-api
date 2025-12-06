pipeline {
    agent any

    parameters {
        string(
            name: 'SERVICES',
            description: 'Comma-separated list of services to deploy (e.g., service1,service2). Use "ALL" to deploy all services.',
            defaultValue: 'ALL'
        )
    }

    environment {
        DEPLOYMENT_SERVER = '192.168.10.46'
        DEPLOYMENT_DIRECTORY = '~/firepulse-api'
        IMAGE_TAG = 'latest'
        ALL_SERVICES = 'config-service,discovery-service,brigadeflow-service,pyrosense-service,stationlogix-service'

        DATABASE_HOST = '192.168.20.150'
        DATABASE_NAME = 'firepulse_db'
    }

    stages {
        stage('Determine Services to Deploy') {
            steps {
                script {
                    // Check if triggered by GitHub Actions via parameters
                    if (params.SERVICES && params.SERVICES != 'ALL') {
                        env.SERVICES_TO_DEPLOY = params.SERVICES
                        echo "Deployment triggered for service: ${params.SERVICES}"
                    } else if (params.SERVICES == 'ALL') {
                        env.SERVICES_TO_DEPLOY = env.ALL_SERVICES
                        echo "Manual deployment of all services"
                    } else {
                        env.SERVICES_TO_DEPLOY = env.ALL_SERVICES
                        echo "Default: deploying all services"
                    }

                    // Calculate deployment order respecting dependencies
                    def services = env.SERVICES_TO_DEPLOY.split(',')
                    def orderedServices = calculateDeploymentOrder(services)
                    env.ORDERED_SERVICES = orderedServices.join(',')
                    echo "Deployment order: ${orderedServices.join(' -> ')}"
                }
            }
        }

        stage('Transfer Configuration') {
            steps {
                script {
                    withCredentials([
                        string(credentialsId: 'ssh-user', variable: 'SSH_USER'),
                        string(credentialsId: 'ssh-password', variable: 'SSH_PASSWORD'),
                        string(credentialsId: 'database-user', variable: 'DATABASE_USER'),
                        string(credentialsId: 'database-password', variable: 'DATABASE_PASSWORD')
                    ]) {
                        // Create/Override .env file on deployment server
                        sh """
                            sshpass -p "${SSH_PASSWORD}" ssh -o StrictHostKeyChecking=no ${SSH_USER}@${DEPLOYMENT_SERVER} '
                                mkdir -p ${DEPLOYMENT_DIRECTORY}
                                echo "DATABASE_HOST=${DATABASE_HOST}" > ${DEPLOYMENT_DIRECTORY}/.env
                                echo "DATABASE_NAME=${DATABASE_NAME}" >> ${DEPLOYMENT_DIRECTORY}/.env
                                echo "DATABASE_USER=${DATABASE_USER}" >> ${DEPLOYMENT_DIRECTORY}/.env
                                echo "DATABASE_PASSWORD=${DATABASE_PASSWORD}" >> ${DEPLOYMENT_DIRECTORY}/.env
                                chmod 600 ${DEPLOYMENT_DIRECTORY}/.env
                            '
                        """
                        // Transfer docker-compose.yaml
                        sh """
                            sshpass -p "${SSH_PASSWORD}" scp -o StrictHostKeyChecking=no docker-compose.yaml ${SSH_USER}@${DEPLOYMENT_SERVER}:${DEPLOYMENT_DIRECTORY}/
                        """
                    }
                }
            }
        }

        stage('Deploy Services') {
            steps {
                script {
                    withCredentials([
                        string(credentialsId: 'ssh-user', variable: 'SSH_USER'),
                        string(credentialsId: 'ssh-password', variable: 'SSH_PASSWORD'),
                        string(credentialsId: 'github-token', variable: 'GITHUB_TOKEN')
                    ]) {
                        def services = env.ORDERED_SERVICES.split(',')
                        sh """
                            sshpass -p "${SSH_PASSWORD}" ssh -o StrictHostKeyChecking=no ${SSH_USER}@${DEPLOYMENT_SERVER} '
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
                                                sshpass -p "${SSH_PASSWORD}" ssh -o StrictHostKeyChecking=no ${SSH_USER}@${DEPLOYMENT_SERVER} '
                                                    set -e
                                                    cd ${DEPLOYMENT_DIRECTORY}
                                                    echo "Pulling updated image for ${service}..."
                                                    export IMAGE_TAG=${IMAGE_TAG}
                                                    docker compose pull ${service}
                                                    echo Starting ${service}..."
                                                    docker compose up -d ${service} --wait
                                                    echo "${service} deployed successfully!"
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
                                sshpass -p "${SSH_PASSWORD}" ssh -o StrictHostKeyChecking=no ${SSH_USER}@${DEPLOYMENT_SERVER} '
                                    docker image prune -f || true
                                    docker logout ghcr.io || true
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
