def call() {
    node {
        stage("docker build main branch") {
            script {
              sh "echo 'docker build'"
            }
        }
    }
}
