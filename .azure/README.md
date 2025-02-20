# Azure Pipeline Configuration

This directory contains the Azure DevOps pipeline configuration for the JPO Conflict Monitor project.

## Overview

The pipeline configuration in `azure-pipelines.yml` is primarily used for Continuous Integration (CI) in the CDOT-CV fork of the JPO Conflict Monitor project. It serves as the first step in a two-stage process:

1. **Build Pipeline (this configuration)**
   - Triggers on changes to the `develop` branch
   - Monitors specific project directories for changes:
     - `jpo-conflictmonitor/*`
   - Copies all source files to the artifact staging directory
   - Publishes the source code as an artifact named 'jpo-conflictmonitor'

2. **Release Pipeline (configured in Azure DevOps)**
   - Uses the published artifact from the build pipeline
   - Handles Docker image building and deployment
   - Configuration is managed through the Azure DevOps web interface

## Pipeline Trigger

The pipeline automatically triggers when:

- Changes are pushed to the `develop` branch
- Changes occur in any of the monitored project directories

## Pipeline Steps

1. **Copy Files**
   - Copies project files to the artifact staging directory
   - Excludes certain files/directories by default:
     - `docs` directories
     - Markdown (`.md`) files
     - `test-message-sender` directory
     - `jpo-utils` directory
     - `samples` directory
     - `.github` directory
     - `.vscode` directory

2. **Publish Artifact**
   - Creates an artifact named 'jpo-conflictmonitor'
   - Makes the source code available for the release pipeline

## Note

The actual Docker build process and deployment steps are configured in the Azure DevOps release pipeline, which is separate from this build pipeline configuration. The release pipeline picks up the artifact produced by this build pipeline and performs the necessary build and deployment steps.
