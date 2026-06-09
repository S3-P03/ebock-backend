pipeline {
    agent {
        docker {
            image 'eclipse-temurin:21-jdk'
            args '-v /var/run/docker.sock:/var/run/docker.sock'
        }
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Build and Test') {
            when {
                anyOf {
                    changeRequest target: 'main'
                    changeRequest target: 'dev'
                }
            }
            steps {
                sh 'chmod +x gradlew'
                sh './gradlew clean test --stacktrace'
            }
        }
    }

    post {
        always {
            junit '**/build/test-results/test/*.xml'
        }
        success {
            echo 'Tests passed'
        }
        failure {
            echo 'Tests failed'
        }
    }
}