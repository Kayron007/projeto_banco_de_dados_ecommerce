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
