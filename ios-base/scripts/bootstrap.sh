#!/bin/zsh

GIT_REPO=$(git rev-parse --show-toplevel)
GIT_DIR=$(git rev-parse --git-dir)

# Install Homebrew
if ! type "brew" > /dev/null; then
    echo '`brew` not found. Install Homebrew'
    ruby -e "$(curl -fsSL https://raw.githubusercontent.com/Homebrew/install/master/install)"
fi

# rbenv
if ! type "rbenv" > /dev/null; then
    echo '`rbenv` not found. Install rbenv'
    brew install rbenv rbenv-communal-gems

    cat << EOS >> ~/.zshrc
    if which rbenv > /dev/null; then eval "\$(rbenv init -)"; fi
    export PATH="\$HOME/.rbenv/bin:\$PATH"
    eval "\$(rbenv init -)"
EOS
fi

source ~/.zshrc

# Ruby
RUBY_VERSION="$(rbenv local)"
if [ "$(rbenv versions --bare | grep ${RUBY_VERSION})" = '' ]; then
    rbenv install ${RUBY_VERSION}
fi

# Dependency for Swiftgen
echo 'Install libxml2'
brew install libxml2

# Install Mint dependencies
if ! type "mint" > /dev/null; then
    echo '`mint` not found. Install Mint'
    git clone https://github.com/yonaskolb/Mint.git
    make -C Mint
    rm -rf Mint
fi

mint bootstrap

# Swiftgen
mkdir -p "${GIT_REPO}/ios-base/DroidKaigi 2020/Generated"
mint run swiftgen swiftgen

# XcodeGen
mint run xcodegen

# Bundle(Gems)
bundle install
bundle exec pod install

# Git-hooks
cp ${GIT_REPO}/ios-base/scripts/git-hooks/* ${GIT_DIR}/hooks
