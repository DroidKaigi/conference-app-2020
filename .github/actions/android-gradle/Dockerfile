FROM circleci/android:api-28

# GitHub Actions restriction
USER root

RUN curl -o /toolkit.sh -sL "https://raw.githubusercontent.com/jmatsu/github-actions-toolkit/f23fcb2f07c2cc309e207e7ccc2a9731e01b4b81/toolkit.sh"

# Overwrite
COPY entry-point.sh /entry-point.sh
RUN chmod +x /entry-point.sh

ENTRYPOINT ["/entry-point.sh"]