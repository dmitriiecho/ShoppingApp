FROM eclipse-temurin:21-jdk-jammy

# Base OS packages needed to fetch the Android SDK and build the project.
RUN apt-get update \
    && apt-get install -y --no-install-recommends \
        ca-certificates \
        curl \
        git \
        unzip \
        sudo \
    && rm -rf /var/lib/apt/lists/*

# Create the non-root "ubuntu" user Cloud Agents run as (uid/gid 1000).
RUN groupadd --gid 1000 ubuntu \
    && useradd --uid 1000 --gid 1000 --create-home --shell /bin/bash ubuntu \
    && echo "ubuntu ALL=(ALL) NOPASSWD:ALL" > /etc/sudoers.d/ubuntu \
    && chmod 0440 /etc/sudoers.d/ubuntu

# Android SDK layout. compileSdk is 37 and build-tools 37.0.0 (see gradle/libs.versions.toml).
ENV ANDROID_HOME=/opt/android-sdk \
    ANDROID_SDK_ROOT=/opt/android-sdk
ENV PATH="${ANDROID_HOME}/cmdline-tools/latest/bin:${ANDROID_HOME}/platform-tools:${PATH}"

# Pinned command-line tools release for reproducible builds.
ARG CMDLINE_TOOLS_VERSION=11076708
ARG ANDROID_PLATFORM="platforms;android-37.0"
ARG ANDROID_BUILD_TOOLS="build-tools;37.0.0"

RUN set -eux; \
    mkdir -p "${ANDROID_HOME}/cmdline-tools"; \
    curl -fsSL -o /tmp/cmdline-tools.zip \
        "https://dl.google.com/android/repository/commandlinetools-linux-${CMDLINE_TOOLS_VERSION}_latest.zip"; \
    unzip -q /tmp/cmdline-tools.zip -d "${ANDROID_HOME}/cmdline-tools"; \
    mv "${ANDROID_HOME}/cmdline-tools/cmdline-tools" "${ANDROID_HOME}/cmdline-tools/latest"; \
    rm /tmp/cmdline-tools.zip; \
    yes | sdkmanager --licenses > /dev/null; \
    sdkmanager --install "platform-tools" "${ANDROID_PLATFORM}" "${ANDROID_BUILD_TOOLS}" > /dev/null; \
    chown -R ubuntu:ubuntu "${ANDROID_HOME}"

USER ubuntu
