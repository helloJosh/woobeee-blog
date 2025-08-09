import { BrowserRouter as Router, Routes, Route } from "react-router-dom"
import { ThemeProvider } from "./components/theme-provider"
import BlogLayout from "./components/blog-layout"
import HomePage from "./pages/home-page"
import CategoryPage from "./pages/category-page"
import PostPage from "./pages/post-page"
import SearchPage from "./pages/search-page"
import NotFoundPage from "./pages/not-found-page"
import "./App.css"

function App() {
  return (
    <ThemeProvider attribute="class" defaultTheme="light" enableSystem>
      <Router>
        <BlogLayout>
          <Routes>
            <Route path="/" element={<HomePage />} />
            <Route path="/blog" element={<HomePage />} />
            <Route path="/blog/category/:categoryId" element={<CategoryPage />} />
            <Route path="/blog/post/:postId" element={<PostPage />} />
            <Route path="/search" element={<SearchPage />} />
            <Route path="*" element={<NotFoundPage />} />
          </Routes>
        </BlogLayout>
      </Router>
    </ThemeProvider>
  )
}

export default App
