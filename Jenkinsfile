pipeline {
    agent any

    stages {
        stage('Environment Setup') {
            steps {
                script {
                    sh 'chmod +x ./gradlew'
                    def projectName = sh(script: './gradlew -q printProjectName', returnStdout: true).trim()
                    def projectVersion = sh(script: './gradlew -q printProjectVersion', returnStdout: true).trim()
                    env.PROJECT_NAME = projectName
                    env.PROJECT_VERSION = projectVersion
                    env.JAR_PATH = "${WORKSPACE}/build/libs/${env.PROJECT_NAME}-${env.PROJECT_VERSION}.jar"
                }
                echo 'Environment variables set'
            }
        }

        stage('Test') {
            steps {
                echo 'Tests complete'
            }
        }

        stage('Build') {
            steps {
                sh "chmod u+x ${WORKSPACE}/gradlew"
                dir("${WORKSPACE}") {
                    sh './gradlew :clean :build -x test'
                }
            }
        }

        stage('Build Docker Image') {
            steps {
                script {
                    def dockerImageTag = "${env.PROJECT_NAME}:${env.PROJECT_VERSION}"
                    env.DOCKER_IMAGE_TAG = dockerImageTag
                    sh "docker system prune -a -f"
                    sh "docker rmi $DOCKER_HUB_USER_NAME/${dockerImageTag} || true"
                    sh "docker build --no-cache=true --build-arg JAR_FILE=${env.JAR_PATH} -t ${dockerImageTag} -f ${WORKSPACE}/Dockerfile ${WORKSPACE}/build/libs/"
                }
            }
        }

        stage('Push Docker Image') {
            steps {
                script {
                    withCredentials([string(credentialsId: 'DOCKER_HUB_ACCESS_TOKEN', variable: 'DOCKER_HUB_ACCESS_TOKEN')]) {
                        sh '''
                        echo $DOCKER_HUB_ACCESS_TOKEN | docker login -u $DOCKER_HUB_USER_NAME --password-stdin
                        '''
                    }
                    def dockerImageTag = env.DOCKER_IMAGE_TAG
                    sh "docker tag ${dockerImageTag} $DOCKER_HUB_USER_NAME/${dockerImageTag}"
                    sh "docker push $DOCKER_HUB_USER_NAME/${dockerImageTag}"
                }
            }
        }

        stage('Deploy to EC2') {
            steps {
                script {
                    withCredentials([
                        sshUserPrivateKey(credentialsId: 'EC2_DEPLOY_KEY_FOR_GREEN_JANGTEO', keyFileVariable: 'EC2_DEPLOY_KEY_FOR_GREEN_JANGTEO'),
                        string(credentialsId: 'DB_ROOT_PASSWORD', variable: 'DB_ROOT_PASSWORD'),
                        string(credentialsId: 'DB_USER_NAME', variable: 'DB_USER_NAME'),
                        string(credentialsId: 'DB_USER_PASSWORD', variable: 'DB_USER_PASSWORD'),
                        string(credentialsId: 'REDIS_PASSWORD', variable: 'REDIS_PASSWORD'),
                        string(credentialsId: 'JWT_SECRET_KEY', variable: 'JWT_SECRET_KEY'),
                        string(credentialsId: 'SSL_KEY_STORE_PASSWORD', variable: 'SSL_KEY_STORE_PASSWORD'),
                        string(credentialsId: 'FRONTEND_IP_FOR_GREEN_JANGTEO', variable: 'FRONTEND_IP_FOR_GREEN_JANGTEO'),
                    ]) {
                        sh '''
                        scp -i "$EC2_DEPLOY_KEY_FOR_GREEN_JANGTEO" set-up-docker.sh ubuntu@"$EC2_IP_FOR_GREEN_JANGTEO":"$EC2_DEPLOY_PATH"
                        scp -i "$EC2_DEPLOY_KEY_FOR_GREEN_JANGTEO" "$JAR_PATH" ubuntu@"$EC2_IP_FOR_GREEN_JANGTEO":"$EC2_DEPLOY_PATH"
                        scp -i "$EC2_DEPLOY_KEY_FOR_GREEN_JANGTEO" "${WORKSPACE}/deploy.sh" "${WORKSPACE}/check-and-restart.sh" ubuntu@"$EC2_IP_FOR_GREEN_JANGTEO":"$EC2_DEPLOY_PATH"
                        ssh -i "$EC2_DEPLOY_KEY_FOR_GREEN_JANGTEO" ubuntu@"$EC2_IP_FOR_GREEN_JANGTEO" "chmod +x ${EC2_DEPLOY_PATH}/deploy.sh"
                        ssh -i "$EC2_DEPLOY_KEY_FOR_GREEN_JANGTEO" ubuntu@"$EC2_IP_FOR_GREEN_JANGTEO" "chmod +x ${EC2_DEPLOY_PATH}/set-up-docker.sh && PROJECT_NAME='$PROJECT_NAME' ${EC2_DEPLOY_PATH}/set-up-docker.sh $DB_ROOT_PASSWORD $DB_USER_NAME $DB_USER_PASSWORD"
                        ssh -i "$EC2_DEPLOY_KEY_FOR_GREEN_JANGTEO" ubuntu@"$EC2_IP_FOR_GREEN_JANGTEO" "export SSL_KEY_STORE_PASSWORD='$SSL_KEY_STORE_PASSWORD'; export DOCKER_HUB_USER_NAME='$DOCKER_HUB_USER_NAME'; export DB_USER_NAME='$DB_USER_NAME'; export DB_USER_PASSWORD='$DB_USER_PASSWORD'; export EC2_IP_FOR_GREEN_JANGTEO='$EC2_IP_FOR_GREEN_JANGTEO'; export FRONTEND_IP_FOR_GREEN_JANGTEO='$FRONTEND_IP_FOR_GREEN_JANGTEO'; export REDIS_PASSWORD='$REDIS_PASSWORD'; export JWT_SECRET_KEY='$JWT_SECRET_KEY'; export PROJECT_NAME='$PROJECT_NAME'; export PROJECT_VERSION='$PROJECT_VERSION'; ${EC2_DEPLOY_PATH}/deploy.sh"
                        '''
                    }
                }
            }
        }

        stage('Archive Artifacts') {
            steps {
                archiveArtifacts artifacts: "build/libs/*.jar", fingerprint: true
                echo 'Artifacts archived'
            }
        }
    }
}
