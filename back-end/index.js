/**
 * @file API para gerenciamento de usuários e consultas
 * @version 1.2.0
 * @description Esta API permite o cadastro, login, obtenção de perfil, atualização e exclusão de usuários, além do gerenciamento de especialidades, regiões, médicos, horários e consultas.
 * @license MIT
 */

const express = require("express");
const app = express();
const sqlite3 = require("sqlite3").verbose();
const bcrypt = require("bcrypt");
const { check, validationResult } = require("express-validator");
const cors = require("cors");

app.use(express.json());
app.use(express.urlencoded({ extended: true }));
app.use(cors());

const DBPATH = "nippodb";
const db = new sqlite3.Database(DBPATH);

/**
 * Rota para cadastrar um novo usuário.
 * @name POST /cadastro
 * @function
 * @memberof module:API
 * @param {string} nome - Nome do usuário.
 * @param {string} email - E-mail do usuário (deve ser único).
 * @param {string} senha - Senha do usuário.
 * @param {string} telefone - Número de telefone do usuário.
 * @param {string} cpf - CPF do usuário.
 * @returns {string} Retorna uma mensagem indicando sucesso ou falha no cadastro.
 */
app.post("/cadastro", [check("email").isEmail()], function (req, res) {
  const errors = validationResult(req);
  if (!errors.isEmpty()) {
    return res.status(400).json({ errors: errors.array() });
  }

  var nome = req.body.nome;
  var email = req.body.email;
  var senha = req.body.senha;
  var telefone = req.body.telefone;
  var cpf = req.body.cpf;

  // Verificar se o e-mail já está cadastrado
  var sqlSelect = `SELECT * FROM usuarios WHERE email = ?`;
  db.get(sqlSelect, [email], function (err, row) {
    if (err) {
      console.error("Erro ao verificar e-mail:", err);
      return res
        .status(500)
        .send("Erro interno do servidor ao verificar e-mail");
    }

    if (row) {
      // Se o e-mail já estiver em uso, retornar um erro
      return res.status(400).json({ error: "E-mail já cadastrado" });
    }

    bcrypt.hash(senha, 10, function (err, hash) {
      if (err) {
        console.error("Erro ao cadastrar usuário:", err);
        return res.status(500).send("Erro ao cadastrar usuário: " + err);
      }

      var sqlInsert = `INSERT INTO usuarios (nome, email, senha, telefone, cpf) VALUES (?, ?, ?, ?, ?)`;
      var params = [nome, email, hash, telefone, cpf];

      db.run(sqlInsert, params, function (err) {
        if (err) {
          console.error("Erro ao inserir usuário no banco de dados:", err);
          return res.status(500).send("Erro ao cadastrar usuário: " + err);
        }

        res.send("Usuário cadastrado com sucesso!");
      });
    });
  });
});

/**
 * Rota para fazer login de um usuário.
 * @name POST /login
 * @function
 * @memberof module:API
 * @param {string} email - E-mail do usuário.
 * @param {string} senha - Senha do usuário.
 * @returns {Object} Retorna uma mensagem indicando sucesso ou falha no login, junto com o ID do usuário.
 */
app.post("/login", function (req, res) {
  var email = req.body.email;
  var senha = req.body.senha;

  var sqlSelect = `SELECT id, senha FROM usuarios WHERE email=?`;
  db.get(sqlSelect, [email], function (err, row) {
    if (err) {
      console.error("Erro ao buscar usuário:", err);
      return res
        .status(500)
        .json({ error: "Erro interno do servidor ao autenticar usuário" });
    }

    if (!row) {
      return res.status(401).json({ error: "Conta não encontrada" });
    }

    bcrypt.compare(senha, row.senha, function (err, result) {
      if (err) {
        console.error("Erro ao comparar senha:", err);
        return res
          .status(500)
          .json({ error: "Erro interno do servidor ao autenticar usuário" });
      }

      if (result) {
        // Se a senha estiver correta, inclua o ID do usuário na resposta JSON
        res.json({ message: "Login bem-sucedido", idUsuario: row.id });
      } else {
        return res.status(401).json({ error: "Senha incorreta" });
      }
    });
  });
});

/**
 * Rota para obter dados do perfil de um usuário pelo ID.
 * @name GET /perfil/:id
 * @function
 * @memberof module:API
 * @param {string} id - ID do usuário.
 * @returns {Object} Retorna os dados do perfil do usuário ou uma mensagem indicando que o usuário não foi encontrado.
 */
app.get("/perfil/:id", function (req, res) {
  var id = req.params.id;

  var sqlSelect = `SELECT nome, email, telefone, cpf FROM usuarios WHERE id=?`;
  db.get(sqlSelect, [id], function (err, row) {
    if (err) {
      console.error("Erro ao buscar dados do usuário:", err);
      return res.status(500).send("Erro ao buscar dados do usuário: " + err);
    }

    if (!row) {
      return res.status(404).send("Usuário não encontrado");
    }

    // Envia os dados do usuário para o frontend
    res.json(row);
  });
});

/**
 * Rota para listar todos os usuários cadastrados.
 * @name GET /usuarios
 * @function
 * @memberof module:API
 * @returns {Array} Retorna uma lista com todos os usuários cadastrados.
 */
app.get("/usuarios", function (req, res) {
  db.all(`SELECT * FROM usuarios`, [], (err, rows) => {
    if (err) {
      res.status(500).send("Erro ao listar usuários: " + err);
    }

    res.send(rows);
  });
});

/**
 * Rota para atualizar os dados de um usuário pelo ID.
 * @name PUT /usuarios/:id
 * @function
 * @memberof module:API
 * @param {string} id - ID do usuário.
 * @param {string} nome - Novo nome do usuário.
 * @param {string} email - Novo e-mail do usuário.
 * @param {string} senha - Nova senha do usuário.
 * @param {string} telefone - Novo número de telefone do usuário.
 * @param {string} cpf - Novo CPF do usuário.
 * @returns {string} Retorna uma mensagem indicando sucesso ou falha na atualização.
 */
app.put("/usuarios/:id", function (req, res) {
  var id = req.params.id;
  var nome = req.body.nome;
  var email = req.body.email;
  var senha = req.body.senha;
  var telefone = req.body.telefone;
  var cpf = req.body.cpf;

  var sqlSelect = `SELECT senha FROM usuarios WHERE id=?`;
  db.get(sqlSelect, [id], function (err, row) {
    if (err) {
      return res.status(500).send("Erro ao atualizar usuário: " + err);
    }

    var hash = row.senha;

    // Verifica se a senha foi alterada
    if (senha) {
      // Se a senha foi alterada, gera o novo hash
      bcrypt.hash(senha, 10, function (err, newHash) {
        if (err) {
          return res.status(500).send("Erro ao atualizar usuário: " + err);
        }

        var sqlUpdate = `UPDATE usuarios SET nome=?, email=?, senha=?, telefone=?, cpf=? WHERE id=?`;
        var params = [nome, email, newHash, telefone, cpf, id];

        db.run(sqlUpdate, params, function (err) {
          if (err) {
            return res.status(500).send("Erro ao atualizar usuário: " + err);
          }

          res.send("Usuário atualizado com sucesso!");
        });
      });
    } else {
      // Se a senha não foi alterada, mantém o hash existente
      var sqlUpdate = `UPDATE usuarios SET nome=?, email=?, telefone=?, cpf=? WHERE id=?`;
      var params = [nome, email, telefone, cpf, id];

      db.run(sqlUpdate, params, function (err) {
        if (err) {
          return res.status(500).send("Erro ao atualizar usuário: " + err);
        }

        res.send("Usuário atualizado com sucesso!");
      });
    }
  });
});

/**
 * Rota para excluir um usuário pelo ID.
 * @name DELETE /usuarios/:id
 * @function
 * @memberof module:API
 * @param {string} id - ID do usuário a ser excluído.
 * @returns {string} Retorna uma mensagem indicando sucesso ou falha na exclusão.
 */
app.delete("/usuarios/:id", function (req, res) {
  var id = req.params.id;

  var sqlDelete = `DELETE FROM usuarios WHERE id=?`;

  db.run(sqlDelete, id, function (err) {
    if (err) {
      return res.status(500).send("Erro ao excluir usuário: " + err);
    }

    res.send("Usuário excluído com sucesso!");
  });
});
app.get("/especialidades", function (req, res) {
  var sqlSelect = "SELECT * FROM especialidades";
  db.all(sqlSelect, [], function (err, rows) {
    if (err) {
      return res.status(500).send("Erro ao buscar especialidades: " + err);
    }
    res.json(rows);
  });
});

app.get("/medicos/:especialidade_id", function (req, res) {
  var especialidade_id = req.params.especialidade_id;
  var sqlSelect = "SELECT * FROM medicos WHERE especialidade_id=?";
  db.all(sqlSelect, [especialidade_id], function (err, rows) {
    if (err) {
      return res.status(500).send("Erro ao buscar médicos: " + err);
    }
    res.json(rows);
  });
});

app.post("/agendamentos", function (req, res) {
  console.log("Recebendo solicitação para /agendamentos");
  console.log("Dados recebidos:", req.body);

  var usuario_id = req.body.usuario_id;
  var medico_id = req.body.medico_id;
  var data = req.body.data;

  if (!usuario_id || !medico_id || !data) {
    console.error(
      "Campos obrigatórios não fornecidos. Dados recebidos:",
      req.body,
    );
    return res.status(400).send("Campos obrigatórios não fornecidos.");
  }

  var sqlInsert =
    "INSERT INTO agendamentos (usuario_id, medico_id, data) VALUES (?, ?, ?)";
  db.run(sqlInsert, [usuario_id, medico_id, data], function (err) {
    if (err) {
      console.error("Erro ao salvar agendamento:", err);
      return res.status(500).send("Erro ao salvar agendamento: " + err);
    }
    res.send("Agendamento salvo com sucesso!");
  });
});
app.get("/horarios/:medicoId", function (req, res) {
  var medicoId = req.params.medicoId;
  var sqlSelect = "SELECT * FROM horarios WHERE medico_id=?";
  db.all(sqlSelect, [medicoId], function (err, rows) {
    if (err) {
      return res.status(500).send("Erro ao buscar horários: " + err);
    }
    res.json(rows);
  });
});

/**
 * Tratamento de erro para rotas não encontradas.
 * @name NotFoundHandler
 * @function
 * @memberof module:API
 * @param {Object} req - Objeto de requisição.
 * @param {Object} res - Objeto de resposta.
 * @param {Function} next - Próxima função de middleware.
 */
app.use(function (req, res, next) {
  res.status(404).send("API NIPPO DENTS");
});

/**
 * Tratamento de erro genérico.
 * @name ErrorHandler
 * @function
 * @memberof module:API
 * @param {Object} err - Objeto de erro.
 * @param {Object} req - Objeto de requisição.
 * @param {Object} res - Objeto de resposta.
 * @param {Function} next - Próxima função de middleware.
 */
app.use(function (err, req, res, next) {
  console.error(err.stack);
  res.status(500).send("Algo deu errado no servidor");
});

const port = process.env.PORT || 3001;
/**
 * Inicia o servidor na porta especificada.
 * @name ServerInit
 * @function
 * @memberof module:API
 */
app.listen(port, function () {
  console.log("Servidor está rodando na porta " + port);
});
