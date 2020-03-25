pipeline {
    agent {
        docker {
            image 'maven:3.6-jdk-11'
            args '-v $HOME:/var/maven --env /etc:/etc --env TNA_HOME=/var/maven/tna/container'
        }
    }
    environment {
        HOME = '.'
        JAVA_TOOL_OPTIONS = '-Duser.home=/var/maven'
        SETTINGS_XML = '/var/maven/settings.xml'
    }
    options {
        skipStagesAfterUnstable()
    }
    stages {
        stage('Build') {
            steps {
                sh 'mvn -B -DskipUT=true -DskipIT=true -s ${SETTINGS_XML} clean package'
            }
        }
        stage('Test') {
            steps {
                sh 'mvn -s ${SETTINGS_XML} -Dskip.npm=true verify'
                jacoco(
                      execPattern: '**/target/*.exec',
                      classPattern: '**/target/classes',
                      sourcePattern: '**/src/main/java',
                      exclusionPattern: '**/src/test*'
                )
            }
            post {
                always {
                    archiveArtifacts artifacts: '**/target/*.zip', fingerprint: true
                    junit 'target/surefire-reports/*.xml'
                    junit 'target/failsafe-reports/*.xml'
                }
            }
        }

    }
}