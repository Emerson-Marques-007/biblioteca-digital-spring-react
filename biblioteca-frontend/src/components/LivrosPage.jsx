import { useState, useEffect } from 'react'
import { Button } from '@/components/ui/button.jsx'
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/card.jsx'
import { Input } from '@/components/ui/input.jsx'
import { Label } from '@/components/ui/label.jsx'
import { Badge } from '@/components/ui/badge.jsx'
import { Dialog, DialogContent, DialogDescription, DialogFooter, DialogHeader, DialogTitle, DialogTrigger } from '@/components/ui/dialog.jsx'
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from '@/components/ui/table.jsx'
import { BookOpen, Plus, Search, Edit, Trash2, CheckCircle, XCircle } from 'lucide-react'

export default function LivrosPage({ apiBaseUrl }) {
  const [livros, setLivros] = useState([])
  const [loading, setLoading] = useState(true)
  const [searchTerm, setSearchTerm] = useState('')
  const [isDialogOpen, setIsDialogOpen] = useState(false)
  const [editingLivro, setEditingLivro] = useState(null)
  const [formData, setFormData] = useState({
    titulo: '',
    autor: '',
    isbn: '',
    anoPublicacao: '',
    genero: ''
  })

  useEffect(() => {
    fetchLivros()
  }, [])

  const fetchLivros = async () => {
    try {
      setLoading(true)
      const response = await fetch(`${apiBaseUrl}/livros`)
      if (response.ok) {
        const data = await response.json()
        setLivros(data)
      } else {
        console.error('Erro ao buscar livros:', response.statusText)
      }
    } catch (error) {
      console.error('Erro ao buscar livros:', error)
    } finally {
      setLoading(false)
    }
  }

  const handleSubmit = async (e) => {
    e.preventDefault()
    
    try {
      const url = editingLivro 
        ? `${apiBaseUrl}/livros/${editingLivro.id}`
        : `${apiBaseUrl}/livros`
      
      const method = editingLivro ? 'PUT' : 'POST'
      
      const response = await fetch(url, {
        method,
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({
          ...formData,
          anoPublicacao: parseInt(formData.anoPublicacao)
        }),
      })

      if (response.ok) {
        await fetchLivros()
        setIsDialogOpen(false)
        resetForm()
        alert(editingLivro ? 'Livro atualizado com sucesso!' : 'Livro criado com sucesso!')
      } else {
        const errorData = await response.json()
        alert(`Erro: ${errorData.message || 'Erro desconhecido'}`)
      }
    } catch (error) {
      console.error('Erro ao salvar livro:', error)
      alert('Erro ao salvar livro')
    }
  }

  const handleEdit = (livro) => {
    setEditingLivro(livro)
    setFormData({
      titulo: livro.titulo,
      autor: livro.autor,
      isbn: livro.isbn,
      anoPublicacao: livro.anoPublicacao.toString(),
      genero: livro.genero || ''
    })
    setIsDialogOpen(true)
  }

  const handleDelete = async (id) => {
    if (window.confirm('Tem certeza que deseja deletar este livro?')) {
      try {
        const response = await fetch(`${apiBaseUrl}/livros/${id}`, {
          method: 'DELETE',
        })

        if (response.ok) {
          await fetchLivros()
          alert('Livro deletado com sucesso!')
        } else {
          const errorData = await response.json()
          alert(`Erro: ${errorData.message || 'Erro ao deletar livro'}`)
        }
      } catch (error) {
        console.error('Erro ao deletar livro:', error)
        alert('Erro ao deletar livro')
      }
    }
  }

  const resetForm = () => {
    setFormData({
      titulo: '',
      autor: '',
      isbn: '',
      anoPublicacao: '',
      genero: ''
    })
    setEditingLivro(null)
  }

  const filteredLivros = livros.filter(livro =>
    livro.titulo.toLowerCase().includes(searchTerm.toLowerCase()) ||
    livro.autor.toLowerCase().includes(searchTerm.toLowerCase()) ||
    livro.isbn.includes(searchTerm)
  )

  if (loading) {
    return (
      <div className="max-w-7xl mx-auto py-6 sm:px-6 lg:px-8">
        <div className="px-4 py-6 sm:px-0">
          <div className="text-center">
            <p>Carregando livros...</p>
          </div>
        </div>
      </div>
    )
  }

  return (
    <div className="max-w-7xl mx-auto py-6 sm:px-6 lg:px-8">
      <div className="px-4 py-6 sm:px-0">
        <div className="mb-8">
          <h1 className="text-3xl font-bold text-gray-900">Gestão de Livros</h1>
          <p className="mt-2 text-gray-600">Gerencie o acervo da biblioteca</p>
        </div>

        <div className="mb-6 flex flex-col sm:flex-row gap-4">
          <div className="flex-1">
            <div className="relative">
              <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400 h-4 w-4" />
              <Input
                placeholder="Buscar por título, autor ou ISBN..."
                value={searchTerm}
                onChange={(e) => setSearchTerm(e.target.value)}
                className="pl-10"
              />
            </div>
          </div>
          
          <Dialog open={isDialogOpen} onOpenChange={setIsDialogOpen}>
            <DialogTrigger asChild>
              <Button onClick={resetForm}>
                <Plus className="h-4 w-4 mr-2" />
                Novo Livro
              </Button>
            </DialogTrigger>
            <DialogContent className="sm:max-w-[425px]">
              <DialogHeader>
                <DialogTitle>
                  {editingLivro ? 'Editar Livro' : 'Novo Livro'}
                </DialogTitle>
                <DialogDescription>
                  {editingLivro 
                    ? 'Edite as informações do livro abaixo.'
                    : 'Preencha as informações do novo livro.'
                  }
                </DialogDescription>
              </DialogHeader>
              <form onSubmit={handleSubmit}>
                <div className="grid gap-4 py-4">
                  <div className="grid gap-2">
                    <Label htmlFor="titulo">Título *</Label>
                    <Input
                      id="titulo"
                      value={formData.titulo}
                      onChange={(e) => setFormData({...formData, titulo: e.target.value})}
                      required
                    />
                  </div>
                  <div className="grid gap-2">
                    <Label htmlFor="autor">Autor *</Label>
                    <Input
                      id="autor"
                      value={formData.autor}
                      onChange={(e) => setFormData({...formData, autor: e.target.value})}
                      required
                    />
                  </div>
                  <div className="grid gap-2">
                    <Label htmlFor="isbn">ISBN *</Label>
                    <Input
                      id="isbn"
                      value={formData.isbn}
                      onChange={(e) => setFormData({...formData, isbn: e.target.value})}
                      placeholder="Ex: 9788535902778"
                      required
                    />
                  </div>
                  <div className="grid gap-2">
                    <Label htmlFor="anoPublicacao">Ano de Publicação *</Label>
                    <Input
                      id="anoPublicacao"
                      type="number"
                      min="1000"
                      max="2030"
                      value={formData.anoPublicacao}
                      onChange={(e) => setFormData({...formData, anoPublicacao: e.target.value})}
                      required
                    />
                  </div>
                  <div className="grid gap-2">
                    <Label htmlFor="genero">Gênero</Label>
                    <Input
                      id="genero"
                      value={formData.genero}
                      onChange={(e) => setFormData({...formData, genero: e.target.value})}
                      placeholder="Ex: Romance, Ficção, etc."
                    />
                  </div>
                </div>
                <DialogFooter>
                  <Button type="button" variant="outline" onClick={() => setIsDialogOpen(false)}>
                    Cancelar
                  </Button>
                  <Button type="submit">
                    {editingLivro ? 'Atualizar' : 'Criar'}
                  </Button>
                </DialogFooter>
              </form>
            </DialogContent>
          </Dialog>
        </div>

        <Card>
          <CardHeader>
            <CardTitle className="flex items-center gap-2">
              <BookOpen className="h-5 w-5" />
              Livros Cadastrados ({filteredLivros.length})
            </CardTitle>
            <CardDescription>
              Lista de todos os livros do acervo
            </CardDescription>
          </CardHeader>
          <CardContent>
            {filteredLivros.length === 0 ? (
              <div className="text-center py-8">
                <BookOpen className="mx-auto h-12 w-12 text-gray-400" />
                <h3 className="mt-2 text-sm font-medium text-gray-900">Nenhum livro encontrado</h3>
                <p className="mt-1 text-sm text-gray-500">
                  {searchTerm ? 'Tente ajustar sua busca.' : 'Comece adicionando um novo livro.'}
                </p>
              </div>
            ) : (
              <Table>
                <TableHeader>
                  <TableRow>
                    <TableHead>Título</TableHead>
                    <TableHead>Autor</TableHead>
                    <TableHead>ISBN</TableHead>
                    <TableHead>Ano</TableHead>
                    <TableHead>Gênero</TableHead>
                    <TableHead>Status</TableHead>
                    <TableHead className="text-right">Ações</TableHead>
                  </TableRow>
                </TableHeader>
                <TableBody>
                  {filteredLivros.map((livro) => (
                    <TableRow key={livro.id}>
                      <TableCell className="font-medium">{livro.titulo}</TableCell>
                      <TableCell>{livro.autor}</TableCell>
                      <TableCell className="font-mono text-sm">{livro.isbn}</TableCell>
                      <TableCell>{livro.anoPublicacao}</TableCell>
                      <TableCell>{livro.genero || '-'}</TableCell>
                      <TableCell>
                        <Badge variant={livro.statusDisponibilidade ? "default" : "secondary"}>
                          {livro.statusDisponibilidade ? (
                            <>
                              <CheckCircle className="h-3 w-3 mr-1" />
                              Disponível
                            </>
                          ) : (
                            <>
                              <XCircle className="h-3 w-3 mr-1" />
                              Emprestado
                            </>
                          )}
                        </Badge>
                      </TableCell>
                      <TableCell className="text-right">
                        <div className="flex justify-end gap-2">
                          <Button
                            variant="outline"
                            size="sm"
                            onClick={() => handleEdit(livro)}
                          >
                            <Edit className="h-4 w-4" />
                          </Button>
                          <Button
                            variant="outline"
                            size="sm"
                            onClick={() => handleDelete(livro.id)}
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

