#!/bin/bash
pwd
# download the PCRE library
curl -OL https://sourceforge.net/projects/pcre/files/pcre/8.45/pcre-8.45.tar.gz/download
tar xvzf pcre2-10.40.tar.gz && rm pcre2-10.40.tar.gz
# download OpenSSL sources and extract
curl -OL https://www.openssl.org/source/openssl-1.1.0.tar.gz
tar xvzf openssl-1.1.0.tar.gz && rm openssl-1.1.0.tar.gz

cd nginx-1.23.1/ || exit
./configure --prefix=. --with-pcre=../pcre2-10.40/ --with-http_ssl_module --with-openssl=../openssl-1.1.0 --http-client-body-temp-path=tmp/client_body_temp/ --http-proxy-temp-path=tmp/proxy_temp/ --http-fastcgi-temp-path=tmp/fastcgi_temp/ --http-uwsgi-temp-path=tmp/uwsgi_temp/ --http-scgi-temp-path=tmp/scgi_temp/ --with-ipv6 --with-http_gzip_static_module --with-http_stub_status_module --with-http_v2_module --without-mail_pop3_module --without-mail_imap_module --without-mail_smtp_module --without-http_fastcgi_module

make && make install >> ./logs/compile.log

