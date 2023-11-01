def call() {
    node {
        stage("docker build main branch") {
            script {
              sh "echo 'docker build'"
              echo "APP_ORIGIN_WORKSPACE = ${APP_ORIGIN_WORKSPACE}"
            }
        }
    }
}
