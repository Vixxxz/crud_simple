document.getElementById('filtroForm').addEventListener('submit', async (e) => {
    e.preventDefault();

    const queryParams = criarQueryParams(new FormData(e.target));

    try {
        const url = `http://localhost:8080/crud_v3_war_exploded/controlecliente?${queryParams}`;
        console.log("URL gerada:", url);

        const resposta = await fetch(url);
        const clientes = await resposta.json();

        if (Array.isArray(clientes)) {  // Verifica se a resposta é um array
            renderTabela(clientes);
        } else {
            console.error('Erro: A resposta não é um array.', clientes);
            alert('Erro ao buscar clientes. A resposta da API não é válida.');
        }
    } catch (error) {
        console.error('Erro ao buscar clientes:', error);
        alert('Erro ao buscar clientes. Por favor, tente novamente.');
    }
});

function renderTabela(clientes) {
    const tbody = document.querySelector('#table-clientes tbody');
    tbody.innerHTML = ''; // Limpa os resultados anteriores

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

    adicionarEventosModais();
}

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

function formatarCartao(cartao) {
    if (!cartao || Object.keys(cartao).length === 0) return 'Cartão não disponível.';
    return `
        <p>Número do Cartão: ${cartao.numero || ''}</p>
        <p>Bandeira: ${cartao.bandeira || ''}</p>
        <p>Data de Vencimento: ${cartao.dataVencimento || ''}</p>
    `;
}

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