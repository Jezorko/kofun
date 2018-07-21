#!/usr/bin/env bash

repositoryBranchName='repository'

function extractMavenProperty() {
    property="${1}"
    mvn -q \
        -Dexec.executable="echo" \
        -Dexec.args='${'"${property}"'}' \
        --non-recursive \
        org.codehaus.mojo:exec-maven-plugin:1.3.1:exec
}

# retrieve project properties from pom
groupId="$(extractMavenProperty 'project.groupId')"
artifactId="$(extractMavenProperty 'project.artifactId')"
projectVersion="$(extractMavenProperty 'project.version')"

echo "Group ID is ${groupId}"
echo "Artifact ID is ${artifactId}"
echo "Project version is ${projectVersion}"

# setup file names
ls target
jarName="${artifactId}-${projectVersion}-jar-with-dependencies.jar"
javadocName="${artifactId}-${projectVersion}-javadoc.jar"
sourcesName="${artifactId}-${projectVersion}-sources.jar"

# setup paths
pathToJar="../target/${jarName}"
pathToJavadoc="../target/${javadocName}"
pathToSources="../target/${sourcesName}"

# checkout to repository branch
# TODO: make this a checkout instead of extra clone
git clone --depth=50 --branch="${repositoryBranchName}" "https://github.com/${TRAVIS_REPO_SLUG}.git" "${repositoryBranchName}"
cd "${repositoryBranchName}"

# install jar
installationPath="${groupId//./\/}/${artifactId//./\/}/${projectVersion}"
mkdir --parents "${installationPath}"
echo mvn install:install-file \
    -Dfile="${pathToJar}" \
    -DgroupId="${groupId}" \
    -DartifactId="${artifactId}" \
    -Dversion="${projectVersion}" \
    -Dpackaging=jar \
    -DgeneratePom=true \
    -DlocalRepositoryPath=. \
    -DcreateChecksum=true

# move javadoc and sources
echo "Adding javadoc and sources to ${installationPath}"
mv "${pathToJavadoc}" "${installationPath}/${javadocName}"
mv "${pathToSources}" "${installationPath}/${sourcesName}"

# list files in installation directory
ls "${installationPath}"

# setup SSH key
sshKeyFileName='id_rsa'
sshDirectory="$(pwd)/.ssh"
sshKeyFilePath="${sshDirectory}/${sshKeyFileName}"

mkdir "${sshDirectory}"
mv "../${sshKeyFileName}" "${sshKeyFilePath}"

chmod 600 "${sshKeyFilePath}"
chmod 700 "${sshDirectory}"

# setup GIT
git config --global user.email "travis@travis-ci.org"
git config --global user.name "Travis CI"
git remote add raw ssh://git@github.com/${TRAVIS_REPO_SLUG}.git

# commit and push
git add "${installationPath}"
git commit "${installationPath}" -m "Release ${artifactId} version ${projectVersion}"
GIT_SSH_COMMAND="ssh -i ${sshKeyFilePath}" git push raw "HEAD:${repositoryBranchName}"