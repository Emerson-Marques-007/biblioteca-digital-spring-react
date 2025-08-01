import { useState, useEffect } from 'react'
import { BrowserRouter as Router, Routes, Route, Link, useLocation } from 'react-router-dom'
import { Button } from '@/components/ui/button.jsx'
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/card.jsx'
import { Tabs, TabsContent, TabsList, TabsTrigger } from '@/components/ui/tabs.jsx'
import { BookOpen, Users, FileText, Home, Plus, Search } from 'lucide-react'
import LivrosPage from './components/LivrosPage.jsx'
import UsuariosPage from './components/UsuariosPage.jsx'
import EmprestimosPage from './components/EmprestimosPage.jsx'
import './App.css'

// Configuração da API para Spring Boot
const API_BASE_URL = 'http://localhost:8080/api'

function Navigation() {
  const location = useLocation()
  
  const navItems = [
    { path: '/', label: 'Dashboard', icon: Home },
    { path: '/livros', label: 'Livros', icon: BookOpen },
    { path: '/usuarios', label: 'Usuários', icon: Users },
    { path: '/emprestimos', label: 'Empréstimos', icon: FileText },
  ]

  return (
    <nav className="bg-white shadow-sm border-b">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div className="flex justify-between h-16">
          <div className="flex">
            <div className="flex-shrink-0 flex items-center">
              <BookOpen className="h-8 w-8 text-blue-600" />
              <span className="ml-2 text-xl font-bold text-gray-900">Biblioteca Digital</span>
            </div>
            <div className="hidden sm:ml-6 sm:flex sm:space-x-8">
              {navItems.map((item) => {
                const Icon = item.icon
                const isActive = location.pathname === item.path
                return (
                  <Link
                    key={item.path}
                    to={item.path}
                    className={`inline-flex items-center px-1 pt-1 border-b-2 text-sm font-medium ${
                      isActive
                        ? 'border-blue-500 text-gray-900'
                        : 'border-transparent text-gray-500 hover:border-gray-300 hover:text-gray-700'
                    }`}
                  >
                    <Icon className="h-4 w-4 mr-2" />
                    {item.label}
                  </Link>
                )
              })}
            </div>
          </div>
        </div>
      </div>
    </nav>
  )
}

function Dashboard() {
  const [stats, setStats] = useState({
    totalLivros: 0,
    totalUsuarios: 0,
    emprestimosAtivos: 0,
    emprestimosAtrasados: 0
  })

  useEffect(() => {
    // Buscar estatísticas do backend Spring Boot
    const fetchStats = async () => {
      try {
        const [livrosRes, usuariosRes, emprestimosRes] = await Promise.all([
          fetch(`${API_BASE_URL}/livros/estatisticas`),
          fetch(`${API_BASE_URL}/usuarios/estatisticas`),
          fetch(`${API_BASE_URL}/emprestimos/estatisticas`)
        ])

        const livrosStats = await livrosRes.json()
        const usuariosStats = await usuariosRes.json()
        const emprestimosStats = await emprestimosRes.json()

        setStats({
          totalLivros: livrosStats.totalLivros || 0,
          totalUsuarios: usuariosStats.totalUsuarios || 0,
          emprestimosAtivos: emprestimosStats.emprestimosAtivos || 0,
          emprestimosAtrasados: emprestimosStats.emprestimosAtrasados || 0
        })
      } catch (error) {
        console.error('Erro ao buscar estatísticas:', error)
        // Valores padrão em caso de erro
        setStats({
          totalLivros: 0,
          totalUsuarios: 0,
          emprestimosAtivos: 0,
          emprestimosAtrasados: 0
        })
      }
    }

    fetchStats()
  }, [])

  const statCards = [
    {
      title: 'Total de Livros',
      value: stats.totalLivros,
      description: 'Livros cadastrados no sistema',
      icon: BookOpen,
      color: 'text-blue-600'
    },
    {
      title: 'Total de Usuários',
      value: stats.totalUsuarios,
      description: 'Usuários cadastrados',
      icon: Users,
      color: 'text-green-600'
    },
    {
      title: 'Empréstimos Ativos',
      value: stats.emprestimosAtivos,
      description: 'Livros emprestados atualmente',
      icon: FileText,
      color: 'text-orange-600'
    },
    {
      title: 'Empréstimos Atrasados',
      value: stats.emprestimosAtrasados,
      description: 'Devoluções em atraso',
      icon: FileText,
      color: 'text-red-600'
    }
  ]

  return (
    <div className="max-w-7xl mx-auto py-6 sm:px-6 lg:px-8">
      <div className="px-4 py-6 sm:px-0">
        <div className="mb-8">
          <h1 className="text-3xl font-bold text-gray-900">Dashboard</h1>
          <p className="mt-2 text-gray-600">Sistema de Gestão de Biblioteca - Spring Boot + React</p>
        </div>

        <div className="grid grid-cols-1 gap-6 sm:grid-cols-2 lg:grid-cols-4 mb-8">
          {statCards.map((stat, index) => {
            const Icon = stat.icon
            return (
              <Card key={index}>
                <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
                  <CardTitle className="text-sm font-medium">{stat.title}</CardTitle>
                  <Icon className={`h-4 w-4 ${stat.color}`} />
                </CardHeader>
                <CardContent>
                  <div className="text-2xl font-bold">{stat.value}</div>
                  <p className="text-xs text-muted-foreground">{stat.description}</p>
                </CardContent>
              </Card>
            )
          })}
        </div>

        <div className="grid grid-cols-1 gap-6 lg:grid-cols-2">
          <Card>
            <CardHeader>
              <CardTitle>Ações Rápidas</CardTitle>
              <CardDescription>Acesso rápido às principais funcionalidades</CardDescription>
            </CardHeader>
            <CardContent className="space-y-4">
              <Link to="/livros">
                <Button className="w-full justify-start" variant="outline">
                  <Plus className="h-4 w-4 mr-2" />
                  Cadastrar Novo Livro
                </Button>
              </Link>
              <Link to="/usuarios">
                <Button className="w-full justify-start" variant="outline">
                  <Plus className="h-4 w-4 mr-2" />
                  Cadastrar Novo Usuário
                </Button>
              </Link>
              <Link to="/emprestimos">
                <Button className="w-full justify-start" variant="outline">
                  <Plus className="h-4 w-4 mr-2" />
                  Realizar Empréstimo
                </Button>
              </Link>
            </CardContent>
          </Card>

          <Card>
            <CardHeader>
              <CardTitle>Informações do Sistema</CardTitle>
              <CardDescription>Detalhes sobre o sistema de gestão</CardDescription>
            </CardHeader>
            <CardContent>
              <div className="space-y-2">
                <p className="text-sm"><strong>Versão:</strong> 2.0.0</p>
                <p className="text-sm"><strong>Desenvolvido por:</strong> Emerson Marques Cardoso dos Santos</p>
                <p className="text-sm"><strong>Backend:</strong> Spring Boot (Java)</p>
                <p className="text-sm"><strong>Frontend:</strong> React + Tailwind CSS</p>
                <p className="text-sm"><strong>Banco de Dados:</strong> H2 Database</p>
                <p className="text-sm"><strong>Conceitos POO:</strong> Encapsulamento, Herança, Polimorfismo, Abstração</p>
              </div>
            </CardContent>
          </Card>
        </div>
      </div>
    </div>
  )
}

function App() {
  return (
    <Router>
      <div className="min-h-screen bg-gray-50">
        <Navigation />
        <main>
          <Routes>
            <Route path="/" element={<Dashboard />} />
            <Route path="/livros" element={<LivrosPage apiBaseUrl={API_BASE_URL} />} />
            <Route path="/usuarios" element={<UsuariosPage apiBaseUrl={API_BASE_URL} />} />
            <Route path="/emprestimos" element={<EmprestimosPage apiBaseUrl={API_BASE_URL} />} />
          </Routes>
        </main>
      </div>
    </Router>
  )
}

export default App

