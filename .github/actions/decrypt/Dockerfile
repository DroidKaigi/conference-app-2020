FROM buildpack-deps:stretch-curl

RUN curl -sSL "https://github.com/stedolan/jq/releases/download/jq-1.6/jq-linux64" -o /jq
RUN chmod +x /jq
RUN ln -s /jq /usr/bin/jq

RUN curl -o /toolkit.sh -sL "https://raw.githubusercontent.com/jmatsu/github-actions-toolkit/f23fcb2f07c2cc309e207e7ccc2a9731e01b4b81/toolkit.sh"

COPY entry-point.sh /entry-point.sh
RUN chmod +x /entry-point.sh

ENTRYPOINT ["/entry-point.sh"]