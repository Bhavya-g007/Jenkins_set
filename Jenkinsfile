pipeline {
    agent any

    echo "Running on branch: ${env.BRANCH_NAME}"
    
    environment {
        JAR_FILE = "java-sample-21-1.0.0.jar"
        APP_PORT = '5000'
    }
    
    tools {
         maven 'Maven-3.9.0'
         jdk 'JDK-21'
     }
        
    stages {
        stage('Checkout') {
            echo '🔄 Checking out source code...'
            steps {
                git url: 'https://github.com/firstcheck-organization/jenkins.git'
            }
        }

        stage('Unit Tests') {
            steps {
                echo '🧪 Running unit tests...'
                dir('javaapp-pipeline') {
                    sh '''
                        echo "Starting test execution..."
                        mvn clean test
                    '''
                }
            }
        }

        stage('Trivy Scan') {
            steps {
                echo '🧪 Running Trivy Scan...'
                dir('javaapp-pipeline') {
                    sh '''
                        wget -q https://raw.githubusercontent.com/aquasecurity/trivy/main/contrib/html.tpl -O html.tpl
                        trivy fs --format template --template "@html.tpl" -o report.html .
                    '''
                }
            }
        }

        stage('Sonar Analysis') {
            steps {
                echo '🧪 Running SonarQube Analysis...'
                dir('javaapp-pipeline') {
                    withSonarQubeEnv('sonar') {
                        sh '''
                            mvn verify sonar:sonar \
                            -Dsonar.projectKey=java-app \
                            -Dsonar.projectName=java-app 
                        '''
                    }
                }
            }
        }

        stage('Build') {
            steps {
                echo '🏗️ Building application...'
                dir('javaapp-pipeline') {
                    sh '''
                        echo "Building JAR file..."
                        mvn clean package -DskipTests=true
                    '''
                }
            }
        }

        stage('Publish to Artifactory') {
            steps {
                echo '🏗️ Uploading artifactory to JFrog...'
                dir('javaapp-pipeline/target') {
                    rtUpload (
                        serverId: 'jfrog',
                        spec: '''{
                            "files": [
                                {
                                    "pattern": “java-sample-21-1.0.0.jar ",
                                    "target": "maven-Bhavya/com/artisantek/java-sample-21/1.0.0/"
                                }
                            ]
                        }'''
                    )
                }
            }
        }
    
        stage('Approval') {
            steps {
                input 'Please approve for deployment'
            }
        }

        stage('Deploy') {
            steps {
                echo '🚀 Deploying application...'
                dir('javaapp-pipeline/target') {
                    sh '''
                        echo "🛑 Stopping any existing application processes..."
                        if pgrep -f "java -jar java-sample-21-1.0.0.jar" > /dev/null; then
                            pkill -f "java -jar java-sample-21-1.0.0.jar"
                            echo "App was running and has been killed."
                        else
                            echo "App is not running."
                        fi
                        JENKINS_NODE_COOKIE=dontKillMe nohup java -jar java-sample-21-1.0.0.jar > app.log 2>&1 &
                    '''
                }
            }
        }
    }
    post {
        //always {
        //    cleanWs()
        //}
        success {
            echo '🎉 Pipeline completed successfully!'
        }
        failure {
            emailext subject: "Pipeline Failed: ${env.JOB_NAME} - ${env.BUILD_NUMBER}",
                     body: "Build failed. Please check the logs at ${env.BUILD_URL}",
                     to: "bhavyags007@gmail.com"
        }
    }
}
