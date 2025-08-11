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

// 사용자 관련 타입 추가
export interface User {
  id: string
  email: string
  name: string
  profileImage?: string
  createdAt: Date
}

export interface AuthResponse {
  user: User
  accessToken: string
  refreshToken: string
}

export interface LoginRequest {
  email: string
  password: string
}

export interface RegisterRequest {
  email: string
  password: string
  name: string
}
