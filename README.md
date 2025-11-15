# 📦 Projeto Banco de Dados – E-commerce

Este repositório contém a **modelagem conceitual, lógica e física** de um banco de dados para um sistema de **E-commerce**, desenvolvido com fins acadêmicos para praticar **modelagem de dados** e **implementação em SQL** para a Universidade Católica de Brasília (UCB).  

## 🚀 Conteúdo do Projeto  

- **Conceitual_Ecommerce.brM3** → Modelo conceitual criado no **brModelo** (DER).  
- **Lógico_Ecommerce.brM3** → Modelo lógico (tradução do conceitual para tabelas e atributos).  
- **Ecommerce_BD.sql** → Script em SQL com a criação das tabelas e restrições do banco de dados.  
- **README.md** → Documentação do projeto.  

## 🛠️ Tecnologias Utilizadas  

- **brModelo** – Modelagem conceitual e lógica.  
- **MySQL** – SGBD utilizado para implementação.  
- **Workbench** – Ambiente para executar os scripts SQL.  
- **MongoDB + mongosh** – Camada NoSQL usada para eventos e cache.  

## Requisitos e Instalação  

1. **MySQL Server 8.x ou 5.7+**  
   - Baixe o instalador oficial em https://dev.mysql.com/downloads/installer/  
   - Durante a instalação selecione o MySQL Server e opcionalmente o MySQL Workbench.  
   - Crie um usuário administrador (diferente do `root`, pois o script bloqueia o root) e anote a senha.  

2. **MySQL Workbench ou cliente CLI**  
   - Pode ser instalado junto com o MySQL Installer ou baixado em https://dev.mysql.com/downloads/workbench/  
   - Use-o para abrir e executar o arquivo `Ecommerce_BD.sql`.  

3. **MongoDB Community Server 4.4+ e mongosh**  
   - Baixe em https://www.mongodb.com/try/download/community e instale o serviço local.  
   - Instale também o shell `mongosh` (disponível no mesmo instalador ou em https://www.mongodb.com/try/download/shell).  
   - Após a instalação, execute `mongosh mongodb_setup.js` na pasta do projeto para criar as coleções e pipelines demonstrativas.  

## ⚙️ Como Executar o Projeto  

1. Clone o repositório:  
   ```bash
   git clone https://github.com/SEU_USUARIO/projeto_banco_de_dados_ecommerce.git
   ```

2. Abra o arquivo `Ecommerce_BD.sql` no MySQL Workbench ou outro cliente SQL.  

3. Execute o script para criar as tabelas do banco de dados:  
   ```sql
   SOURCE Ecommerce_BD.sql;
   ```

4. O banco estará pronto para novas inserções de dados e consultas.  

## 📊 Estrutura do Banco  

O modelo foi desenvolvido em **3ª Forma Normal (3FN)**, com foco em:  
- Evitar redundância de dados.  
- Garantir integridade referencial.  
- Representar entidades principais: **Cliente**, **Produto**, **Fornecedor**, **Pedido**, **Pagamento**, entre outras.   

## 🤝 Contribuições  

Contribuições são bem-vindas!  
Você pode:  
- Sugerir melhorias na modelagem.  
- Adicionar dados de exemplo para testes.  
- Criar consultas SQL avançadas e relatórios.  

## 📜 Licença  

O MySQL é de código aberto, o que significa que qualquer pessoa pode fazer download do software MySQL pela internet e usá-lo gratuitamente. As organizações também podem alterar seu código de origem para atender às suas necessidades. O software MySQL usa a Licença Pública Geral GNU (GNU General Public License, GPL), que é um conjunto comum de regras para definir o que pode ou não ser feito com o software em várias situações. Consulte a página [Políticas legais do MySQL](https://www.mysql.com/about/legal/) para obter mais informações sobre licenciamento.  

## MongoDB complementar

Complementarmente ao MySQL, o sistema utiliza um script MongoDB (mongodb_setup.js) para armazenar eventos de pedidos e caches de catalogo em um banco orientado a documentos, justificando o uso de um NoSQL para dados semi estruturados e consultas analiticas em tempo real. Execute o script no mongosh para criar a base EcommerceRealtime, coleções, índices e pipelines comentados que explicam como o MongoDB se integra ao fluxo transacional descrito acima.
