pipeline {
	agent any

	environment {
		DOCKER_CREDENTIALS_ID = 'dockerhub-cred'
		CONTAINER_NAME = 'calculator-cli'
		DOCKER_NAMESPACE = 'sartma'
		DOCKER_REPOSITORY = 'calculator'
	}

	stages {
		stage('Checkout') {
			steps {
				checkout([$class: 'GitSCM',
  branches: [[name: '*/main']],
  userRemoteConfigs: [[
    url: 'https://github.com/SartMa/se_lab_ci_cd.git',
    credentialsId: 'github-cred'
  ]]
])
			}
		}

		stage('Prepare Workspace') {
			steps {
				sh 'rm -f *.class'
			}
		}

		stage('Build Application') {
			steps {
				sh 'javac Calculator.java CalculatorTest.java'
			}
		}

		stage('Run Tests') {
			steps {
				sh 'java CalculatorTest'
			}
		}

		stage('Build Docker Image') {
			steps {
				script {
					def imageTag = "${env.DOCKER_NAMESPACE}/${env.DOCKER_REPOSITORY}:${env.BUILD_NUMBER}"
					sh "docker build -t ${imageTag} ."
					env.IMAGE = imageTag
				}
			}
		}

		stage('Push Docker Image') {
			steps {
				withCredentials([
					usernamePassword(
						credentialsId: env.DOCKER_CREDENTIALS_ID,
						usernameVariable: 'DOCKERHUB_USER',
						passwordVariable: 'DOCKERHUB_TOKEN'
					)
				]) {
					sh """
						echo "\$DOCKERHUB_TOKEN" | docker login -u "\$DOCKERHUB_USER" --password-stdin
						docker push ${env.IMAGE}
						docker logout
					"""
				}
			}
		}

		stage('Deploy Container') {
			steps {
				sh """
					docker pull ${env.IMAGE}
					docker stop ${env.CONTAINER_NAME} || true
					docker rm ${env.CONTAINER_NAME} || true
					docker run -d --name ${env.CONTAINER_NAME} ${env.IMAGE}
				"""
			}
		}
	}

	post {
		success {
			echo 'Pipeline finished successfully.'
		}
	}
}
