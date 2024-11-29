// Função que realiza a consulta e preenche a tabela com os dados
async function realizarConsultaClientes() {
    // Selecione o formulário de filtro
    const filtroForm = document.getElementById('filtroForm');

    // Cria os parâmetros de consulta (caso não tenha filtros, ele vai enviar uma consulta "em branco")
    const queryParams = criarQueryParams(new FormData(filtroForm));

    try {
        const url = `http://localhost:8080/crud_v3_war_exploded/controlecliente?${queryParams}`;
        console.log("URL gerada:", url);

        // Faz a requisição à API
        const resposta = await fetch(url);

        // Verifica se a resposta foi bem-sucedida (status 200)
        if (!resposta.ok) {
            throw new Error(`Erro na requisição: ${resposta.statusText}`);
        }

        // Converte a resposta em JSON
        const respostaJson = await resposta.json();

        // Verifica se a resposta contém a propriedade 'dados' e se ela é um array
        if (respostaJson.dados && Array.isArray(respostaJson.dados)) {
            const clientes = respostaJson.dados;
            renderTabela(clientes);  // Chama a função para renderizar a tabela
        } else {
            console.error('Erro: A resposta não contém a propriedade "dados" ou ela não é um array.', respostaJson);
            alert('Erro ao buscar clientes. A resposta da API não é válida.');
        }
    } catch (error) {
        // Se houve algum erro na requisição ou no processamento, exibe a mensagem de erro
        console.error('Erro ao buscar clientes:', error);
        alert('Erro ao buscar clientes. Por favor, tente novamente.');
    }
}

// Chama a função de consulta ao carregar a página
document.addEventListener('DOMContentLoaded', (event) => {
    realizarConsultaClientes();  // Realiza a consulta assim que a página for carregada
});

document.getElementById('filtroForm').addEventListener('submit', async (e) => {
    e.preventDefault();
    realizarConsultaClientes();  // Realiza a consulta ao enviar o formulário de filtro
});

// Função que renderiza os dados na tabela
function renderTabela(clientes) {
    const tbody = document.querySelector('#table-clientes tbody');
    tbody.innerHTML = ''; // Limpa os resultados anteriores

    // Itera sobre os clientes para criar as linhas da tabela
    clientes.forEach(cliente => {
        const tr = document.createElement('tr');
        tr.innerHTML = `
            <td>${cliente.id || ''}</td>
            <td>${cliente.nome || ''}</td>
            <td>${cliente.cpf || ''}</td>
            <td>${cliente.dataNascimento || ''}</td>
            <td>${cliente.telefone || ''}</td>
            <td>${cliente.email || ''}</td>
            <td>
                <button class="btn-endereco btn btn-sm btn-primary" data-id="${cliente.id}" 
                    data-endereco='${JSON.stringify(cliente.endereco || {})}'>Endereço</button>
                <button class="btn-cartao btn btn-sm btn-secondary" data-id="${cliente.id}" 
                    data-cartao='${JSON.stringify(cliente.cartao || {})}'>Cartão</button>
            </td>
        `;
        tbody.appendChild(tr);
    });

    // Adiciona os eventos para os modais de endereço e cartão
    adicionarEventosModais();
}

// Função para adicionar os eventos de modais
function adicionarEventosModais() {
    // Botões de endereço
    document.querySelectorAll('.btn-endereco').forEach(btn => {
        btn.addEventListener('click', (e) => {
            const endereco = JSON.parse(e.target.getAttribute('data-endereco'));
            exibirModal('modalEndereco', formatarEndereco(endereco));
        });
    });

    // Botões de cartão
    document.querySelectorAll('.btn-cartao').forEach(btn => {
        btn.addEventListener('click', (e) => {
            const cartao = JSON.parse(e.target.getAttribute('data-cartao'));
            exibirModal('modalCartao', formatarCartao(cartao));
        });
    });
}

// Função para exibir o modal
function exibirModal(modalId, conteudo) {
    const modal = document.getElementById(modalId);
    const infoDiv = modal.querySelector('.modal-content');
    infoDiv.innerHTML = `
        <div class="modal-header">
            <h3>${modalId === 'modalEndereco' ? 'Endereço' : 'Cartão'}</h3>
            <button class="close-modal btn btn-danger btn-sm">Fechar</button>
        </div>
        <div class="modal-body">${conteudo}</div>
    `;
    modal.style.display = 'flex';

    // Evento para fechar o modal
    modal.querySelector('.close-modal').addEventListener('click', () => {
        modal.style.display = 'none';
    });
}

// Função para formatar o endereço
function formatarEndereco(endereco) {
    if (!endereco || Object.keys(endereco).length === 0) return 'Endereço não disponível.';
    return `
        <p>Rua: ${endereco.tipoLogradouro || ''} ${endereco.logradouro || ''}</p>
        <p>Número: ${endereco.numero || ''}</p>
        <p>Bairro: ${endereco.bairro || ''}</p>
        <p>Cidade: ${endereco.cidade || ''}</p>
        <p>Estado: ${endereco.estado || ''}</p>
    `;
}

// Função para formatar os dados do cartão
function formatarCartao(cartao) {
    if (!cartao || Object.keys(cartao).length === 0) return 'Cartão não disponível.';
    return `
        <p>Número do Cartão: ${cartao.numero || ''}</p>
        <p>Bandeira: ${cartao.bandeira || ''}</p>
        <p>Data de Vencimento: ${cartao.dataVencimento || ''}</p>
    `;
}

// Função para criar os parâmetros de consulta (query string)
function criarQueryParams(formData) {
    const params = new URLSearchParams();

    // Itera sobre os pares chave/valor do FormData
    for (const [key, value] of formData.entries()) {
        if (value.trim() !== "") { // Ignora campos vazios
            params.append(key, value.trim());
        }
    }

    return params.toString(); // Retorna os parâmetros no formato query string
}