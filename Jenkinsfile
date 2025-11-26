pipeline {
	agent any

	parameters {
		string(name: 'ROLL_NUMBER', defaultValue: 'imt2023014', description: 'Used for Docker Hub namespace/tag (lowercase recommended).')
		string(name: 'DOCKER_REPOSITORY', defaultValue: 'calculator', description: 'Docker Hub repository that lives under your namespace.')
	}

	environment {
		DOCKER_CREDENTIALS_ID = 'dockerhub-cred'
		IMAGE = ''
		CONTAINER_NAME = 'calculator-cli'
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
					env.IMAGE = "${params.ROLL_NUMBER.toLowerCase()}/${params.DOCKER_REPOSITORY}:${env.BUILD_NUMBER}"
				}
				sh 'docker build -t $IMAGE .'
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
					sh '''
						echo $DOCKERHUB_TOKEN | docker login -u $DOCKERHUB_USER --password-stdin
						docker push $IMAGE
						docker logout
					'''
				}
			}
		}

		stage('Deploy Container') {
			steps {
				sh '''
					docker pull $IMAGE
					docker stop $CONTAINER_NAME || true
					docker rm $CONTAINER_NAME || true
					docker run -d --name $CONTAINER_NAME $IMAGE
				'''
			}
		}
	}

	post {
		success {
			echo 'Pipeline finished successfully.'
		}
	}
}
