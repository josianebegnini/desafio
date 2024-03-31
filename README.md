Cadastrar uma nova pauta

- POST /api/pauta/cadastrar
{
  "titulo": "Pauta 1",
  "descricao": "Descrição da pauta 1"
}

Abrir uma sessão de votação em uma pauta (a sessão de votação deve ficar aberta por um tempo determinado na chamada de abertura ou 1 minuto por default)
 
- POST /api/sessao/abrirSessao
{
  "nome": "Nome da Sessão",
  "descricao": "Descrição da Sessão",
  "dtSessao": "2024-03-27T10:00:00Z",
  "duracao": 0,
  "fechada": false,
  "pauta": {
    "idPauta": 1
  }
}

Receber votos dos associados em pautas (os votos são apenas 'Sim'/'Não'. Cada associado é identificado por um id único e pode votar apenas uma vez por pauta);

- POST /api/associado/cadastrar
{
  "cpf": "26981994070",
  "nome": "Associado Um"
}

- POST endpoint /api/votacao/votar
{
  "dtVoto": "2024-03-27T10:00:00Z",
  "voto": "SIM",
  "sessao": {
    "idSessao": 1,
    "pauta": {
      "idPauta": 1
    }
  },
  "associado": {
    "cpf": "26981994070"
  }

}

Contabilizar votos
- GET /api/votacao/contabilizar/1
  O serviço de contabilização irá postar uma mensagem na fila queue-desafio
