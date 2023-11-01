def call() {
    def command = 'cd ${APP_ORIGIN_WORKSPACE} && find * -maxdepth 0 -type d'
    def output = ['bash', '-c', command].execute().in.text
    def directories = output.trim().split('\r?\n')
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
