def call() {
    // check for directories located under app directory 
    def directories = sh(script: 'cd ${APP_ORIGIN_WORKSPACE}/app && find * -maxdepth 0 -type d', returnStdout: true).trim().split('\n')
    // check for a single Dockerfile under app directory
    def dockerfile = sh(script: 'cd ${APP_ORIGIN_WORKSPACE}/app && find Dockerfile', returnStatus: true) //find: Dockerfile: No such file or directory
    //def dockerfile = sh(script: 'cd ${APP_ORIGIN_WORKSPACE}/app && find Dockerfile', returnStdout: true).trim().split('\n') //ERROR: script returned exit code 1
    //def dockerfile = sh(script: 'cd ${APP_ORIGIN_WORKSPACE}/app && find Dockerfile', returnStdout: true) //ERROR: script returned exit code 1
    // check for Dockerfiles located in nested directories under app directory
    def dockerfiles = sh(script: 'cd ${APP_ORIGIN_WORKSPACE}/app && find */Dockerfile', returnStdout: true).trim().split('\n')
    node {
        stage("docker build main branch") {
            script {
              sh "echo 'docker build'"
              sh "echo 'APP_ORIGIN_WORKSPACE = ${APP_ORIGIN_WORKSPACE}'"

              //File dockerfile = new File("${APP_ORIGIN_WORKSPACE}/app/Dockerfile")

              // check command exit code
              //sh "echo 'dockerfile: ${dockerfile}'"
              if (dockerfile == 0) {
                println("cd ${APP_ORIGIN_WORKSPACE}/app/ && docker build -t app:latest .")
              } else {
                println("file ${APP_ORIGIN_WORKSPACE}/app/Dockerfile doesn't exist")
              }

              if (directories.first() != "" && dockerfiles.first() != "") {
                  // there are at least one or more directories and Dockerfiles
                  for( app in directories ) {
                      println("check ${app} directory for a Dockerfile")
                      for( this_dockerfile in dockerfiles) {
                        if (this_dockerfile.contains('${app}')) {
                          println("cd ${APP_ORIGIN_WORKSPACE}/app/${app} && docker build -t ${app} .")
                          //def build_image = sh(script: 'cd ${APP_ORIGIN_WORKSPACE}/app/${app} && docker build -t ${app} Dockerfile')
                        }
                      }
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
