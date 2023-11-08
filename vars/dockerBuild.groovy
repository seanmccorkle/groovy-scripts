def call() {
    def directories = sh(script: 'cd ${APP_ORIGIN_WORKSPACE}/app && find * -maxdepth 0 -type d', returnStdout: true).trim().split('\n')
    node {
        stage("docker build main branch") {
            script {
              sh "echo 'docker build'"
              sh "echo 'APP_ORIGIN_WORKSPACE = ${APP_ORIGIN_WORKSPACE}'"
              File dockerfile = new File("${APP_ORIGIN_WORKSPACE}/app/Dockerfile")

              if (dockerfile.exists()) {
                  println("Build container image using Dockerfile")
              } else {
                  println("Check directories for Dockerfiles")
                  if (directories.first() != "") {
                      // there are multiple directories possibly containing Dockerfiles
                      for( app in directories ) {
                          println app
                      }
              }
                
              //if (directories.first() != "") {
              //  // there are multiple directories possibly containing Dockerfiles
              //  for( app in directories ) {
              //    println app
              //    def dockerfile = findFiles(glob: '*Dockerfile*')
              //    sh "echo '${dockerfile}'"
              //  }
              //} else {
              //   // there are no directories so check for a Dockerfile
              //    def dockerfile = findFiles(glob: '*Dockerfile*')
              //    sh "echo '${dockerfile}'"
              //}
            }
        }
    }
}
