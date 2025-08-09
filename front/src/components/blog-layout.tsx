"use client"

import { useState, type ReactNode } from "react"
import Header from "./header"
import Sidebar from "./sidebar"
import { mockCategories } from "../lib/mock-data"

interface BlogLayoutProps {
  children: ReactNode
}

export default function BlogLayout({ children }: BlogLayoutProps) {
  const [sidebarOpen, setSidebarOpen] = useState(true)
  const [sidebarWidth, setSidebarWidth] = useState(320) // 320px 기본값

  return (
    <div className="min-h-screen bg-background">
      <Header onToggleSidebar={() => setSidebarOpen(!sidebarOpen)} sidebarWidth={sidebarWidth} />

      <div className="flex">
        <Sidebar
          categories={mockCategories}
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
