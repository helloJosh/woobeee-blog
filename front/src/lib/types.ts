export interface Category {
  id: string
  name: string
  postCount: number
  children?: Category[]
}

export interface Comment {
  id: string
  author: string
  content: string
  createdAt: Date
  replies: Comment[]
}

export interface Post {
  id: string
  title: string
  content: string
  category: string
  categoryId: string
  views: number
  likes: number
  createdAt: Date
  comments: Comment[]
}
