"use client"

import { Link } from "react-router-dom"
import { Button } from "../components/ui/button"
import { Card, CardContent, CardHeader } from "../components/ui/card"
import { Home, ArrowLeft } from "lucide-react"

export default function NotFoundPage() {
  return (
    <div className="flex items-center justify-center min-h-[60vh]">
      <Card className="w-full max-w-md">
        <CardHeader className="text-center">
          <h1 className="text-6xl font-bold text-muted-foreground mb-4">404</h1>
          <h2 className="text-2xl font-semibold">페이지를 찾을 수 없습니다</h2>
          <p className="text-muted-foreground mt-2">요청하신 페이지가 존재하지 않거나 이동되었을 수 있습니다.</p>
        </CardHeader>
        <CardContent className="flex flex-col gap-3">
          <Button asChild className="w-full">
            <Link to="/blog">
              <Home className="h-4 w-4 mr-2" />
              홈으로 돌아가기
            </Link>
          </Button>
          <Button variant="outline" onClick={() => window.history.back()} className="w-full">
            <ArrowLeft className="h-4 w-4 mr-2" />
            이전 페이지로
          </Button>
        </CardContent>
      </Card>
    </div>
  )
}
