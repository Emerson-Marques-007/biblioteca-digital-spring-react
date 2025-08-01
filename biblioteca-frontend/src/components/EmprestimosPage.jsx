import { useState, useEffect } from 'react'
import { Button } from '@/components/ui/button.jsx'
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/card.jsx'
import { Input } from '@/components/ui/input.jsx'
import { Label } from '@/components/ui/label.jsx'
import { Dialog, DialogContent, DialogDescription, DialogHeader, DialogTitle, DialogTrigger } from '@/components/ui/dialog.jsx'
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from '@/components/ui/table.jsx'
import { Badge } from '@/components/ui/badge.jsx'
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from '@/components/ui/select.jsx'
import { Plus, Search, RotateCcw, CheckCircle, AlertTriangle, FileText } from 'lucide-react'

function EmprestimosPage() {
  const [emprestimos, setEmprestimos] = useState([])
  const [usuarios, setUsuarios] = useState([])
  const [livros, setLivros] = useState([])
  const [loading, setLoading] = useState(true)
  const [searchTerm, setSearchTerm] = useState('')
  const [statusFilter, setStatusFilter] = useState('todos')
  const [isDialogOpen, setIsDialogOpen] = useState(false)
  const [formData, setFormData] = useState({
    usuario_id: '',
    livro_id: '',
    dias_emprestimo: '14'
  })

  useEffect(() => {
    fetchData()
  }, [])

  const fetchData = async () => {
    try {
      const [emprestimosRes, usuariosRes, livrosRes] = await Promise.all([
        fetch('http://localhost:8080/api/emprestimos'),
        fetch('http://localhost:8080/api/usuarios?ativo=true'),
        fetch('http://localhost:8080/api/livros?disponivel=true')
      ])

      const emprestimosData = await emprestimosRes.json()
      const usuariosData = await usuariosRes.json()
      const livrosData = await livrosRes.json()

      // Log detalhado do formato dos dados recebidos
      console.log('Dados brutos de empréstimos:', emprestimosData)

      // Aceita array direto ou objeto com chave 'emprestimos'
      let listaEmprestimos = []
      if (Array.isArray(emprestimosData)) {
        listaEmprestimos = emprestimosData
      } else if (Array.isArray(emprestimosData.emprestimos)) {
        listaEmprestimos = emprestimosData.emprestimos
      } else if (emprestimosData.content && Array.isArray(emprestimosData.content)) {
        listaEmprestimos = emprestimosData.content
      } else {
        // Tenta extrair array de qualquer chave
        for (const key in emprestimosData) {
          if (Array.isArray(emprestimosData[key])) {
            listaEmprestimos = emprestimosData[key]
            break
          }
        }
      }
      setEmprestimos(listaEmprestimos)
      setUsuarios(Array.isArray(usuariosData) ? usuariosData : (usuariosData.usuarios || []))
      setLivros(Array.isArray(livrosData) ? livrosData : (livrosData.livros || []))
    } catch (error) {
      console.error('Erro ao buscar dados:', error)
    } finally {
      setLoading(false)
    }
  }

  const handleSubmit = async (e) => {
    e.preventDefault()
    try {
      // Calcula dataDevolucaoPrevista
      const dias = parseInt(formData.dias_emprestimo)
      const hoje = new Date()
      const dataDevolucaoPrevista = new Date(hoje.getTime() + dias * 24 * 60 * 60 * 1000)
      const response = await fetch('http://localhost:8080/api/emprestimos', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({
          usuarioId: parseInt(formData.usuario_id),
          livroId: parseInt(formData.livro_id),
          dataDevolucaoPrevista: dataDevolucaoPrevista.toISOString()
        }),
      })

      if (response.ok) {
        await fetchData()
        setIsDialogOpen(false)
        resetForm()
        setStatusFilter('todos') // Garante que todos os empréstimos sejam exibidos
        setSearchTerm('') // Limpa busca para garantir exibição
        // Log dos dados recebidos
        const emprestimosRes = await fetch('http://localhost:8080/api/emprestimos')
        const emprestimosData = await emprestimosRes.json()
        console.log('Empréstimos após cadastro:', emprestimosData)
      } else {
        let errorMsg = 'Erro ao criar empréstimo';
        try {
          const error = await response.json();
          errorMsg = error.erro || error.message || JSON.stringify(error) || errorMsg;
        } catch (err) {
          errorMsg = await response.text();
        }
        alert(errorMsg);
      }
    } catch (error) {
      console.error('Erro ao criar empréstimo:', error)
      alert('Erro ao criar empréstimo')
    }
  }

  const handleDevolucao = async (emprestimoId) => {
    if (confirm('Confirma a devolução deste livro?')) {
      try {
        const response = await fetch(`http://localhost:8080/api/emprestimos/${emprestimoId}/devolver`, {
          method: 'PUT',
        })

        if (response.ok) {
          await fetchData()
        } else {
          let errorMsg = 'Erro ao devolver livro';
          try {
            const text = await response.text();
            errorMsg = text || errorMsg;
          } catch (err) {}
          alert(errorMsg);
        }
      } catch (error) {
        console.error('Erro ao devolver livro:', error)
        alert('Erro ao devolver livro')
      }
    }
  }

  const handleRenovacao = async (emprestimoId) => {
    if (confirm('Confirma a renovação deste empréstimo por mais 14 dias?')) {
      try {
        const response = await fetch(`http://localhost:8080/api/emprestimos/${emprestimoId}/renovar`, {
          method: 'PUT',
          headers: {
            'Content-Type': 'application/json',
          },
          body: JSON.stringify({ dias_renovacao: 14 }),
        })

        if (response.ok) {
          await fetchData()
        } else {
          let errorMsg = 'Erro ao renovar empréstimo';
          try {
            const text = await response.text();
            errorMsg = text || errorMsg;
          } catch (err) {}
          alert(errorMsg);
        }
      } catch (error) {
        console.error('Erro ao renovar empréstimo:', error)
        alert('Erro ao renovar empréstimo')
      }
    }
  }

  const resetForm = () => {
    setFormData({
      usuario_id: '',
      livro_id: '',
      dias_emprestimo: '14'
    })
  }

  const getStatusBadge = (status) => {
    switch (status) {
      case 'ATIVO':
        return <Badge variant="default">Ativo</Badge>
      case 'DEVOLVIDO':
        return <Badge variant="secondary">Devolvido</Badge>
      case 'ATRASADO':
        return <Badge variant="destructive">Atrasado</Badge>
      default:
        return <Badge variant="outline">{status}</Badge>
    }
  }

  const filteredEmprestimos = emprestimos.filter(emprestimo => {
    // Compatibilidade de campos camelCase e snake_case
    const usuario = emprestimo.usuario || emprestimo.usuarioDTO || { nome: emprestimo.nomeUsuario };
    const livro = emprestimo.livro || emprestimo.livroDTO || { titulo: emprestimo.tituloLivro, autor: emprestimo.autorLivro };
    const statusEmprestimo = emprestimo.status_emprestimo || emprestimo.statusEmprestimo;

    const nomeUsuario = usuario.nome ? usuario.nome.toLowerCase() : '';
    const tituloLivro = livro.titulo ? livro.titulo.toLowerCase() : '';
    const autorLivro = livro.autor ? livro.autor.toLowerCase() : '';

    const matchesSearch =
      nomeUsuario.includes(searchTerm.toLowerCase()) ||
      tituloLivro.includes(searchTerm.toLowerCase()) ||
      autorLivro.includes(searchTerm.toLowerCase());

    const matchesStatus = statusFilter === 'todos' || statusEmprestimo === statusFilter.toUpperCase();

    return matchesSearch && matchesStatus;
  })

  return (
    <div className="max-w-7xl mx-auto py-6 sm:px-6 lg:px-8">
      <div className="px-4 py-6 sm:px-0">
        <div className="flex justify-between items-center mb-8">
          <div>
            <h1 className="text-3xl font-bold text-gray-900">Gestão de Empréstimos</h1>
            <p className="mt-2 text-gray-600">Gerencie os empréstimos de livros</p>
          </div>
          <Dialog open={isDialogOpen} onOpenChange={setIsDialogOpen}>
            <DialogTrigger asChild>
              <Button onClick={resetForm}>
                <Plus className="h-4 w-4 mr-2" />
                Novo Empréstimo
              </Button>
            </DialogTrigger>
            <DialogContent className="sm:max-w-[425px]">
              <DialogHeader>
                <DialogTitle>Novo Empréstimo</DialogTitle>
                <DialogDescription>
                  Registre um novo empréstimo de livro.
                </DialogDescription>
              </DialogHeader>
              <form onSubmit={handleSubmit} className="space-y-4">
                <div>
                  <Label htmlFor="usuario_id">Usuário</Label>
                  <Select value={formData.usuario_id} onValueChange={(value) => setFormData({ ...formData, usuario_id: value })}>
                    <SelectTrigger>
                      <SelectValue placeholder="Selecione um usuário" />
                    </SelectTrigger>
                    <SelectContent>
                      {usuarios.map((usuario) => (
                        <SelectItem key={usuario.id} value={usuario.id.toString()}>
                          {usuario.nome}
                        </SelectItem>
                      ))}
                    </SelectContent>
                  </Select>
                </div>
                <div>
                  <Label htmlFor="livro_id">Livro</Label>
                  <Select value={formData.livro_id} onValueChange={(value) => setFormData({ ...formData, livro_id: value })}>
                    <SelectTrigger>
                      <SelectValue placeholder="Selecione um livro" />
                    </SelectTrigger>
                    <SelectContent>
                      {livros.map((livro) => (
                        <SelectItem key={livro.id} value={livro.id.toString()}>
                          {livro.titulo}
                        </SelectItem>
                      ))}
                    </SelectContent>
                  </Select>
                </div>
                <div>
                  <Label htmlFor="dias_emprestimo">Dias de Empréstimo</Label>
                  <Input
                    id="dias_emprestimo"
                    type="number"
                    value={formData.dias_emprestimo}
                    onChange={(e) => setFormData({ ...formData, dias_emprestimo: e.target.value })}
                    min="1"
                    max="30"
                    required
                  />
                </div>
                <div className="flex justify-end space-x-2">
                  <Button type="button" variant="outline" onClick={() => setIsDialogOpen(false)}>
                    Cancelar
                  </Button>
                  <Button type="submit">
                    Criar Empréstimo
                  </Button>
                </div>
              </form>
            </DialogContent>
          </Dialog>
        </div>

        <Card>
          <CardHeader>
            <CardTitle>Lista de Empréstimos</CardTitle>
            <CardDescription>
              {filteredEmprestimos.length} empréstimo(s) encontrado(s)
            </CardDescription>
            <div className="flex items-center space-x-4">
              <div className="flex items-center space-x-2">
                <Search className="h-4 w-4 text-gray-400" />
                <Input
                  placeholder="Buscar por usuário, título ou autor..."
                  value={searchTerm}
                  onChange={(e) => setSearchTerm(e.target.value)}
                  className="max-w-sm"
                />
              </div>
              <Select value={statusFilter} onValueChange={setStatusFilter}>
                <SelectTrigger className="w-[180px]">
                  <SelectValue placeholder="Filtrar por status" />
                </SelectTrigger>
                <SelectContent>
                  <SelectItem value="todos">Todos</SelectItem>
                  <SelectItem value="ativo">Ativo</SelectItem>
                  <SelectItem value="devolvido">Devolvido</SelectItem>
                  <SelectItem value="atrasado">Atrasado</SelectItem>
                </SelectContent>
              </Select>
            </div>
          </CardHeader>
          <CardContent>
            {loading ? (
              <div className="text-center py-4">Carregando...</div>
            ) : (
              <Table>
                <TableHeader>
                  <TableRow>
                    <TableHead>Usuário</TableHead>
                    <TableHead>Livro</TableHead>
                    <TableHead>Data Empréstimo</TableHead>
                    <TableHead>Data Prevista</TableHead>
                    <TableHead>Data Devolução</TableHead>
                    <TableHead>Status</TableHead>
                    <TableHead>Multa</TableHead>
                    <TableHead>Ações</TableHead>
                  </TableRow>
                </TableHeader>
                <TableBody>
                  {filteredEmprestimos.map((emprestimo) => {
                    // Compatibilidade de campos camelCase e snake_case
                    const usuario = emprestimo.usuario || emprestimo.usuarioDTO || { nome: emprestimo.nomeUsuario };
                    const livro = emprestimo.livro || emprestimo.livroDTO || { titulo: emprestimo.tituloLivro, autor: emprestimo.autorLivro };
                    const dataEmprestimo = emprestimo.data_emprestimo || emprestimo.dataEmprestimo;
                    const dataDevolucaoPrevista = emprestimo.data_devolucao_prevista || emprestimo.dataDevolucaoPrevista;
                    const dataDevolucaoReal = emprestimo.data_devolucao_real || emprestimo.dataDevolucaoReal;
                    const statusEmprestimo = emprestimo.status_emprestimo || emprestimo.statusEmprestimo;
                    const valorMulta = emprestimo.valor_multa !== undefined ? emprestimo.valor_multa : (emprestimo.valorMulta !== undefined ? emprestimo.valorMulta : 0);
                    return (
                      <TableRow key={emprestimo.id}>
                        <TableCell className="font-medium">
                          {usuario.nome || 'N/A'}
                        </TableCell>
                        <TableCell>
                          <div>
                            <div className="font-medium">{livro.titulo || 'N/A'}</div>
                            <div className="text-sm text-gray-500">{livro.autor || 'N/A'}</div>
                          </div>
                        </TableCell>
                        <TableCell>
                          {dataEmprestimo ? new Date(dataEmprestimo).toLocaleDateString('pt-BR') : '-'}
                        </TableCell>
                        <TableCell>
                          {dataDevolucaoPrevista ? new Date(dataDevolucaoPrevista).toLocaleDateString('pt-BR') : '-'}
                        </TableCell>
                        <TableCell>
                          {dataDevolucaoReal
                            ? new Date(dataDevolucaoReal).toLocaleDateString('pt-BR')
                            : '-'
                          }
                        </TableCell>
                        <TableCell>
                          {getStatusBadge(statusEmprestimo)}
                        </TableCell>
                        <TableCell>
                          {valorMulta > 0
                            ? `R$ ${valorMulta.toFixed(2)}`
                            : '-'
                          }
                        </TableCell>
                        <TableCell>
                          <div className="flex space-x-2">
                            {statusEmprestimo === 'ATIVO' && (
                              <>
                                <Button
                                  size="sm"
                                  variant="outline"
                                  onClick={() => handleDevolucao(emprestimo.id)}
                                  title="Devolver livro"
                                >
                                  <CheckCircle className="h-4 w-4" />
                                </Button>
                                <Button
                                  size="sm"
                                  variant="outline"
                                  onClick={() => handleRenovacao(emprestimo.id)}
                                  title="Renovar empréstimo"
                                >
                                  <RotateCcw className="h-4 w-4" />
                                </Button>
                              </>
                            )}
                            {statusEmprestimo === 'ATRASADO' && (
                              <Button
                                size="sm"
                                variant="outline"
                                onClick={() => handleDevolucao(emprestimo.id)}
                                title="Devolver livro"
                              >
                                <CheckCircle className="h-4 w-4" />
                              </Button>
                            )}
                          </div>
                        </TableCell>
                      </TableRow>
                    );
                  })}
                </TableBody>
              </Table>
            )}
          </CardContent>
        </Card>
      </div>
    </div>
  )
}

export default EmprestimosPage

