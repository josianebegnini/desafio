# Use a imagem oficial do RabbitMQ com gerenciamento como base
FROM rabbitmq:3-management

# Defina o usuario e a senha padrao para o RabbitMQ
ENV RABBITMQ_DEFAULT_USER=admin \
    RABBITMQ_DEFAULT_PASS=admin

# Exponha as portas necessarias
EXPOSE 5672 15672

# Copie os dados do volume local para o diretorio do RabbitMQ
VOLUME /var/lib/rabbitmq/