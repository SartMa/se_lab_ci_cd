pipeline {
	agent any

	parameters {
		string(name: 'ROLL_NUMBER', defaultValue: 'cs21b000', description: 'Used for Docker Hub namespace/tag (lowercase recommended).')
		string(name: 'DOCKER_REPOSITORY', defaultValue: 'calculator', description: 'Docker Hub repository that lives under your namespace.')
	}

	environment {
		DOCKER_CREDENTIALS_ID = 'dockerhub-creds'
	}

	stages {
		stage('Checkout') {
			steps {
				checkout scm
			}
		}

		stage('Compile') {
			steps {
				sh 'rm -f *.class'
				sh 'javac Calculator.java CalculatorTest.java'
			}
		}

		stage('Unit Tests') {
			steps {
				sh 'java CalculatorTest'
			}
		}

		stage('Docker Image Tag') {
			steps {
				script {
					env.DOCKER_IMAGE = "${params.ROLL_NUMBER.toLowerCase()}/${params.DOCKER_REPOSITORY}:${env.BUILD_NUMBER}"
				}
			}
		}

		stage('Docker Build') {
			steps {
				sh 'docker build -t $DOCKER_IMAGE .'
				sh 'docker images | grep $DOCKER_IMAGE'
			}
		}

		stage('Docker Push') {
			steps {
				withCredentials([
					usernamePassword(
						credentialsId: env.DOCKER_CREDENTIALS_ID,
						usernameVariable: 'DOCKERHUB_USER',
						passwordVariable: 'DOCKERHUB_TOKEN'
					)
				]) {
					sh 'echo $DOCKERHUB_TOKEN | docker login -u $DOCKERHUB_USER --password-stdin'
					sh 'docker push $DOCKER_IMAGE'
					sh 'docker logout'
				}
			}
		}
	}

	post {
		success {
			echo 'Pipeline finished successfully.'
		}
	}
}
