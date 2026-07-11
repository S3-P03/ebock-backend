pipeline {
    agent any

    environment {
        IMAGE_UPLOAD_DIRECTORY = '/tmp/uploads'
    }

    stages {

        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Start fresh PostgreSQL') {
            steps {
                sh 'docker compose -f /home/debian up -d --force-recreate --renew-anon-volumes postgres'
            }
        }

        stage('Wait for PostgreSQL') {
            steps {
                sh '''
                apt-get update >/dev/null 2>&1 || true
                apt-get install -y postgresql-client >/dev/null 2>&1 || true

                until PGPASSWORD=postgres pg_isready \
                    -h postgres \
                    -U postgres
                do
                    sleep 2
                done
                '''
            }
        }

        stage('Reset Database') {
            steps {
                sh '''
                PGPASSWORD=postgres psql \
                    -h postgres \
                    -U postgres \
                    -d testdb \
                    -c "DROP SCHEMA IF EXISTS ebock CASCADE;"

                PGPASSWORD=postgres psql \
                    -h postgres \
                    -U postgres \
                    -d testdb \
                    -c "CREATE SCHEMA IF NOT EXISTS ebock;"
                '''
            }
        }

        stage('Initialize Database') {
            steps {
                sh '''
                PGPASSWORD=postgres psql \
                    -h postgres \
                    -U postgres \
                    -d testdb \
                    -f src/test/resources/schema.sql
                '''
            }
        }

        stage('Build and Test') {
            steps {
                sh 'chmod +x gradlew'
                sh './gradlew clean test \
                      -Dquarkus.datasource.jdbc.url=jdbc:postgresql://postgres:5432/testdb \
                      -Dquarkus.datasource.username=postgres \
                      -Dquarkus.datasource.password=postgres \
                      -Dquarkus.datasource.devservices.enabled=false \
                      -Dquarkus.devservices.enabled=false \
                      -Dquarkus.profile=ci \
                      --stacktrace'
            }
        }
    }

    post {
        always {
            junit '**/build/test-results/test/*.xml'
        }
    }
}