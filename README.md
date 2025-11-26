# Java Calculator CI/CD

A simple Java-based CLI calculator that supports addition, subtraction, multiplication, and division. The repository is wired for an automated Jenkins pipeline that compiles the sources, runs unit tests, builds a Docker image, verifies it via `docker images`, and pushes it to Docker Hub under your roll number namespace.

## Roll-number mapping

- If your roll number % 2 == 0, you are responsible for the **Java CLI calculator** application described here.
- Use your roll number (lowercase, no spaces) as the Docker Hub namespace, e.g., `cs21b123/calculator`.

## Prerequisites

| Tool | Purpose | Install steps |
| --- | --- | --- |
| Java 17+ JDK | Compile and run the CLI | `sudo apt install openjdk-17-jdk` (Ubuntu/Debian) |
| Docker Engine | Container build/run | [Docker Engine install](https://docs.docker.com/engine/install/) — on Ubuntu:<br>`sudo apt-get update && sudo apt-get install ca-certificates curl gnupg`<br>`curl -fsSL https://download.docker.com/linux/ubuntu/gpg | sudo gpg --dearmor -o /usr/share/keyrings/docker.gpg`<br>`echo "deb [arch=$(dpkg --print-architecture) signed-by=/usr/share/keyrings/docker.gpg] https://download.docker.com/linux/ubuntu $(lsb_release -cs) stable" | sudo tee /etc/apt/sources.list.d/docker.list`<br>`sudo apt-get update && sudo apt-get install docker-ce docker-ce-cli containerd.io`<br>`sudo systemctl enable --now docker` |
| Jenkins | Automate the pipeline | Linux service install: [instructions](https://www.jenkins.io/doc/book/installing/linux/). On Ubuntu:<br>`curl -fsSL https://pkg.jenkins.io/debian-stable/jenkins.io-2023.key | sudo tee /usr/share/keyrings/jenkins-keyring.asc > /dev/null`<br>`echo "deb [signed-by=/usr/share/keyrings/jenkins-keyring.asc] https://pkg.jenkins.io/debian-stable binary/" | sudo tee /etc/apt/sources.list.d/jenkins.list > /dev/null`<br>`sudo apt-get update && sudo apt-get install jenkins`<br>`sudo systemctl enable --now jenkins`<br>Windows users: download the [Jenkins Windows installer](https://www.jenkins.io/download/) and run it as a service. |
| Docker Hub account | Push images | Sign up at [hub.docker.com](https://hub.docker.com/), create a repository named `calculator` under your roll number namespace. |

> **Tip:** Add your user to the `docker` group (`sudo usermod -aG docker $USER`) and re-login so Jenkins and local shells can run Docker commands without `sudo`.

## Local development workflow

1. Compile and run tests:

```bash
javac Calculator.java CalculatorTest.java
java CalculatorTest
```

2. Use the CLI directly:

```bash
java Calculator add 4 6
```

3. Build and run the Docker image locally:

```bash
docker build -t <roll-number>/calculator:local .
docker run --rm <roll-number>/calculator:local add 2 3
```

The default container command (`add 1 1`) prints a quick sanity value; override the command to run other operations.

## Jenkins pipeline setup

1. Ensure Jenkins has the **Git**, **Pipeline**, and **Docker** related plugins (Docker Pipeline + Docker Commons) installed.
2. Create credentials:
   - `dockerhub-creds`: type **Username with password** for your Docker Hub account.
3. Create a new **Pipeline** job:
   - Point the SCM to your GitHub repository URL containing this code.
   - Under *Build Triggers*, enable whatever automation you prefer (Poll SCM, GitHub webhooks, manual builds, etc.).
   - Pipeline definition: *Pipeline script from SCM* → `Jenkinsfile` at repo root.
4. Define the parameters requested in the Jenkinsfile when you run the job:
   - `ROLL_NUMBER`: e.g., `cs21b123` (lowercase). This maps to the Docker namespace and enforces the roll-number %2 selection.
   - `DOCKER_REPOSITORY`: typically `calculator`.

## What the Jenkinsfile does

1. **Checkout** — pulls code from GitHub using the job’s SCM configuration.
2. **Compile** — runs `javac Calculator.java CalculatorTest.java` to ensure sources build.
3. **Unit Tests** — executes `java CalculatorTest`. Any failure halts the pipeline.
4. **Docker Image Tag** — computes `docker.io/<roll-number>/<repo>:<build-number>` and exports it to later stages.
5. **Docker Build** — builds the Docker image and immediately lists `docker images` to confirm creation.
6. **Docker Push** — logs into Docker Hub with `dockerhub-creds` and pushes the freshly built image.

On success you will see the image in Docker Hub under your roll number namespace. Jenkins automatically surfaces the console output showing `docker images` and the push digest.

## Verifying the Docker image

After the pipeline finishes:

```bash
docker pull <roll-number>/calculator:<build-number>
docker run --rm <roll-number>/calculator:<build-number> multiply 3 7
```

You can also confirm via `docker images | grep calculator` either on Jenkins or your development machine.

## Troubleshooting

- **Division by zero:** The CLI throws `IllegalArgumentException`. Tests enforce this behavior; update Jenkinsfile and Docker image only after tests pass.
- **Docker push failures:** Check that the Jenkins agent has Docker installed, the daemon running, and the `dockerhub-creds` credential configured. Make sure your Docker Hub repo exists and is public (or that your account has permission to push private images).
- **Roll number mismatch:** If your roll number % 2 ≠ 0, swap to the application assigned by the rubric before running the pipeline.

With Docker and Jenkins installed (Linux service or Windows installer), this setup provides a fully automated path from GitHub to a Docker Hub image for the Java CLI calculator.
