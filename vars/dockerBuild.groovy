def call() {
    def directories = sh(script: 'cd ${APP_ORIGIN_WORKSPACE}/app && find * -maxdepth 0 -type d', returnStdout: true)
    node {
        stage("docker build main branch") {
            script {
              sh "echo 'docker build'"
              sh "echo 'APP_ORIGIN_WORKSPACE = ${APP_ORIGIN_WORKSPACE}'"
              for( app in directories ) {
                  println app
              }
            }
        }
    }
}
