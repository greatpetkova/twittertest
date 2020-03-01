# Twitter test local environment.
Author: Greta Petkova

## Follow these steps to install this environment on your computer (Mac or Linux):

### 1. Install Docker

Install Docker using [this download link](https://download.docker.com/mac/edge/Docker.dmg) for Mac or [this tutorial](https://www.digitalocean.com/community/tutorials/how-to-install-and-use-docker-on-ubuntu-18-04) for linux.

Verify Docker installation:

```bash
docker -v
```
If everything is alright, you should see something like this:
```bash
Docker version 19.03.5, build 633a0ea
```

### 2. Clone or download this repository

To clone the repository run:
```bash
git clone git@github.com:greatpetkova/twittertest.git
```
If everything is alright, you should see something like this:
```bash
Cloning into 'twittertest'...
remote: Enumerating objects: 3, done.
remote: Counting objects: 100% (3/3), done.
remote: Total 3 (delta 0), reused 0 (delta 0), pack-reused 0
Receiving objects: 100% (3/3), done.
```

### 3. Build Docker container

Open "Terminal" on your computer and navigate to the folder that you clonned or downloaded the repository.

To build Docker container run:

```bash
./bin/build.sh
```
If everything is alright, you should see something like this:
```bash
Successfully built 527d6eb73b9c
Successfully tagged twitter_test:latest
```

### 4. Run twitter tests

Open "Terminal" on your computer and navigate to the folder that you clonned or downloaded the repository.

To run twitter tests run:

```bash
./bin/run.sh
```
If everything is alright, you should see something like this:
```bash
Tests run: 5, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 32.084 sec - in Twitter_Test

Results :

Tests run: 5, Failures: 0, Errors: 0, Skipped: 0
```

Enjoy :)
