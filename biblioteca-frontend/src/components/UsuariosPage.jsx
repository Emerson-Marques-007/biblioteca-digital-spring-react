import { useState, useEffect } from 'react'
import { Button } from '@/components/ui/button.jsx'
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/card.jsx'
import { Input } from '@/components/ui/input.jsx'
import { Label } from '@/components/ui/label.jsx'
import { Dialog, DialogContent, DialogDescription, DialogHeader, DialogTitle, DialogTrigger } from '@/components/ui/dialog.jsx'
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from '@/components/ui/table.jsx'
import { Badge } from '@/components/ui/badge.jsx'
import { Textarea } from '@/components/ui/textarea.jsx'
import { Plus, Search, Edit, Trash2, Users, UserCheck, UserX } from 'lucide-react'

function UsuariosPage() {
  const [usuarios, setUsuarios] = useState([])
  const [loading, setLoading] = useState(true)
  const [searchTerm, setSearchTerm] = useState('')
  const [isDialogOpen, setIsDialogOpen] = useState(false)
  const [editingUsuario, setEditingUsuario] = useState(null)
  const [formData, setFormData] = useState({
    nome: '',
    email: '',
    telefone: '',
    endereco: '',
    statusAtivo: true
  })

  useEffect(() => {
    fetchUsuarios()
  }, [])

  const fetchUsuarios = async () => {
    try {
      const response = await fetch('http://localhost:8080/api/usuarios')
      const data = await response.json()
      // Se vier como array direto, usa data; se vier como objeto, usa data.usuarios
      if (Array.isArray(data)) {
        setUsuarios(data)
      } else {
        setUsuarios(data.usuarios || [])
      }
    } catch (error) {
      console.error('Erro ao buscar usuários:', error)
    } finally {
      setLoading(false)
    }
  }

  const handleSubmit = async (e) => {
    e.preventDefault()
    try {
      // Monta objeto apenas com campos esperados pelo backend
      const usuarioPayload = {
        nome: formData.nome,
        email: formData.email,
        telefone: formData.telefone,
        endereco: formData.endereco,
        statusAtivo: formData.statusAtivo
      }
      const url = editingUsuario ? `http://localhost:8080/api/usuarios/${editingUsuario.id}` : 'http://localhost:8080/api/usuarios'
      const method = editingUsuario ? 'PUT' : 'POST'
      const response = await fetch(url, {
        method,
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify(usuarioPayload),
      })
      if (response.ok) {
        await fetchUsuarios()
        setIsDialogOpen(false)
        resetForm()
      } else {
        let errorMsg = 'Erro ao salvar usuário'
        try {
          const error = await response.json()
          errorMsg = error.erro || error.message || errorMsg
        } catch {}
        alert(errorMsg)
      }
    } catch (error) {
      console.error('Erro ao salvar usuário:', error)
      alert('Erro ao salvar usuário')
    }
  }

  const handleEdit = (usuario) => {
    setEditingUsuario(usuario)
    setFormData({
      nome: usuario.nome || '',
      email: usuario.email || '',
      telefone: usuario.telefone || '',
      endereco: usuario.endereco || '',
      statusAtivo: usuario.statusAtivo
    })
    setIsDialogOpen(true)
  }

  const handleDelete = async (id) => {
    if (confirm('Tem certeza que deseja deletar este usuário?')) {
      try {
        const response = await fetch(`http://localhost:8080/api/usuarios/${id}`, {
          method: 'DELETE',
        })

        if (response.ok) {
          await fetchUsuarios()
        } else {
          const error = await response.json()
          alert(error.erro || 'Erro ao deletar usuário')
        }
      } catch (error) {
        console.error('Erro ao deletar usuário:', error)
        alert('Erro ao deletar usuário')
      }
    }
  }

  const handleToggleStatus = async (id, ativar) => {
    try {
    const endpoint = ativar ? 'ativar' : 'desativar'
    const response = await fetch(`http://localhost:8080/api/usuarios/${id}/${endpoint}`, {
      method: 'PUT',
    })

      if (response.ok) {
        await fetchUsuarios()
      } else {
        const error = await response.json()
        alert(error.erro || `Erro ao ${ativar ? 'ativar' : 'desativar'} usuário`)
      }
    } catch (error) {
      console.error(`Erro ao ${ativar ? 'ativar' : 'desativar'} usuário:`, error)
      alert(`Erro ao ${ativar ? 'ativar' : 'desativar'} usuário`)
    }
  }

  const resetForm = () => {
    setFormData({
      nome: '',
      email: '',
      telefone: '',
      endereco: '',
      statusAtivo: true
    })
    setEditingUsuario(null)
  }

  const filteredUsuarios = usuarios.filter(usuario =>
    usuario.nome.toLowerCase().includes(searchTerm.toLowerCase()) ||
    usuario.email.toLowerCase().includes(searchTerm.toLowerCase())
  )

  return (
    <div className="max-w-7xl mx-auto py-6 sm:px-6 lg:px-8">
      <div className="px-4 py-6 sm:px-0">
        <div className="flex justify-between items-center mb-8">
          <div>
            <h1 className="text-3xl font-bold text-gray-900">Gestão de Usuários</h1>
            <p className="mt-2 text-gray-600">Gerencie os usuários da biblioteca</p>
          </div>
          <Dialog open={isDialogOpen} onOpenChange={setIsDialogOpen}>
            <DialogTrigger asChild>
              <Button onClick={resetForm}>
                <Plus className="h-4 w-4 mr-2" />
                Novo Usuário
              </Button>
            </DialogTrigger>
            <DialogContent className="sm:max-w-[425px]">
              <DialogHeader>
                <DialogTitle>
                  {editingUsuario ? 'Editar Usuário' : 'Novo Usuário'}
                </DialogTitle>
                <DialogDescription>
                  {editingUsuario ? 'Edite as informações do usuário.' : 'Adicione um novo usuário ao sistema.'}
                </DialogDescription>
              </DialogHeader>
              <form onSubmit={handleSubmit} className="space-y-4">
                <div>
                  <Label htmlFor="nome">Nome</Label>
                  <Input
                    id="nome"
                    value={formData.nome}
                    onChange={(e) => setFormData({ ...formData, nome: e.target.value })}
                    required
                  />
                </div>
                <div>
                  <Label htmlFor="email">Email</Label>
                  <Input
                    id="email"
                    type="email"
                    value={formData.email}
                    onChange={(e) => setFormData({ ...formData, email: e.target.value })}
                    required
                  />
                </div>
                <div>
                  <Label htmlFor="telefone">Telefone</Label>
                  <Input
                    id="telefone"
                    value={formData.telefone}
                    onChange={(e) => setFormData({ ...formData, telefone: e.target.value })}
                  />
                </div>
                <div>
                  <Label htmlFor="endereco">Endereço</Label>
                  <Textarea
                    id="endereco"
                    value={formData.endereco}
                    onChange={(e) => setFormData({ ...formData, endereco: e.target.value })}
                    rows={3}
                  />
                </div>
                <div className="flex items-center space-x-2">
                  <input
                    type="checkbox"
                    id="statusAtivo"
                    checked={formData.statusAtivo}
                    onChange={async (e) => {
                      const novoStatus = e.target.checked;
                      setFormData({ ...formData, statusAtivo: novoStatus });
                      if (editingUsuario) {
                        // Chama backend para ativar/desativar
                        const endpoint = novoStatus ? 'ativar' : 'desativar';
                        await fetch(`http://localhost:8080/api/usuarios/${editingUsuario.id}/${endpoint}`, {
                          method: 'PUT',
                        });
                        await fetchUsuarios();
                      }
                    }}
                  />
                  <Label htmlFor="statusAtivo">Usuário Ativo</Label>
                </div>
                <div className="flex justify-end space-x-2">
                  <Button type="button" variant="outline" onClick={() => setIsDialogOpen(false)}>
                    Cancelar
                  </Button>
                  <Button type="submit">
                    {editingUsuario ? 'Atualizar' : 'Criar'}
                  </Button>
                </div>
              </form>
            </DialogContent>
          </Dialog>
        </div>

        <Card>
          <CardHeader>
            <CardTitle>Lista de Usuários</CardTitle>
            <CardDescription>
              {filteredUsuarios.length} usuário(s) encontrado(s)
            </CardDescription>
            <div className="flex items-center space-x-2">
              <Search className="h-4 w-4 text-gray-400" />
              <Input
                placeholder="Buscar por nome ou email..."
                value={searchTerm}
                onChange={(e) => setSearchTerm(e.target.value)}
                className="max-w-sm"
              />
            </div>
          </CardHeader>
          <CardContent>
            {loading ? (
              <div className="text-center py-4">Carregando...</div>
            ) : (
              <Table>
                <TableHeader>
                  <TableRow>
                    <TableHead>Nome</TableHead>
                    <TableHead>Email</TableHead>
                    <TableHead>Telefone</TableHead>
                    <TableHead>Status</TableHead>
                    <TableHead>Data Cadastro</TableHead>
                    <TableHead>Ações</TableHead>
                  </TableRow>
                </TableHeader>
                <TableBody>
                  {filteredUsuarios.map((usuario) => (
                    <TableRow key={usuario.id}>
                      <TableCell className="font-medium">{usuario.nome}</TableCell>
                      <TableCell>{usuario.email}</TableCell>
                      <TableCell>{usuario.telefone || '-'}</TableCell>
                      <TableCell>
                        <Badge variant={usuario.statusAtivo ? 'default' : 'secondary'}>
                          {usuario.statusAtivo ? 'Ativo' : 'Inativo'}
                        </Badge>
                      </TableCell>
                      <TableCell>
                        {usuario.dataCadastro ?
                          new Date(usuario.dataCadastro).toLocaleDateString('pt-BR') :
                          (usuario.data_cadastro ? new Date(usuario.data_cadastro).toLocaleDateString('pt-BR') : '-')}
                      </TableCell>
                      <TableCell>
                        <div className="flex space-x-2">
                          <Button
                            size="sm"
                            variant="outline"
                            onClick={() => handleEdit(usuario)}
                          >
                            <Edit className="h-4 w-4" />
                          </Button>
                          <Button
                            size="sm"
                            variant="outline"
                            onClick={() => handleToggleStatus(usuario.id, !usuario.statusAtivo)}
                          >
                            {usuario.statusAtivo ? (
                              <UserX className="h-4 w-4" />
                            ) : (
                              <UserCheck className="h-4 w-4" />
                            )}
                          </Button>
                          <Button
                            size="sm"
                            variant="outline"
                            onClick={() => handleDelete(usuario.id)}
                          >
                            <Trash2 className="h-4 w-4" />
                          </Button>
                        </div>
                      </TableCell>
                    </TableRow>
                  ))}
                </TableBody>
              </Table>
            )}
          </CardContent>
        </Card>
      </div>
    </div>
  )
}

export default UsuariosPage

