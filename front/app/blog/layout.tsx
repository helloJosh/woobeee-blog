"use client"

import {useState, type ReactNode, useEffect} from "react"
import Header from "@/components/header"
import Sidebar from "@/components/sidebar"
import { mockCategories } from "@/lib/mock-data"
import {categoryAPI} from "@/lib/api";

interface BlogLayoutProps {
    children: ReactNode
}

export default function BlogLayout({ children }: BlogLayoutProps) {
    const [sidebarOpen, setSidebarOpen] = useState(true)
    const [sidebarWidth, setSidebarWidth] = useState(320) // 320px 기본값
    const [categories, setCategories] = useState<any[]>([])
    const [loading, setLoading] = useState(true)

    useEffect(() => {
        const fetchCategories = async () => {
            try {
                const data = await categoryAPI.categories()
                setCategories(data) // ← API에서 받은 데이터 저장
            } catch (err) {
                console.error("카테고리 가져오기 실패:", err)
            } finally {
                setLoading(false)
            }
        }

        fetchCategories()
    }, [])

    return (
        <div className="min-h-screen bg-background">
            <Header onToggleSidebar={() => setSidebarOpen(!sidebarOpen)} sidebarWidth={sidebarWidth} />

            <div className="flex">
                <Sidebar
                    categories={categories}
                    isOpen={sidebarOpen}
                    width={sidebarWidth}
                    onWidthChange={setSidebarWidth}
                />

                <main
                    className={`flex-1 transition-all duration-300`}
                    style={{
                        marginLeft: sidebarOpen ? `${sidebarWidth}px` : "0px",
                    }}
                >
                    <div className="p-6">{children}</div>
                </main>
            </div>
        </div>
    )
}
