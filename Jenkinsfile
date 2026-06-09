pipeline {
    agent any

    stages {

        stage('Checkout') {
            steps {
                checkout scm
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
                    -c "DROP SCHEMA public CASCADE;"

                PGPASSWORD=postgres psql \
                    -h postgres \
                    -U postgres \
                    -d testdb \
                    -c "CREATE SCHEMA public;"
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