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
                sh '''
                    docker rm -f postgres || true
                    docker run -d --name postgres \
                        --network debian_default \
                        -e POSTGRES_DB=tesdb \
                        -e POSTGRES_USER=postgres \
                        -e POSTGRES_PASSWORD=postgres \
                        -p 5433:5432 \
                        --memory=800m \
                        postgres:17 \
                        postgres -c shared_buffers=256MB -c work_mem=16MB -c max_connections=10
                '''
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