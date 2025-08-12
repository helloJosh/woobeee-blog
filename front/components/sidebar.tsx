"use client"

import type React from "react"

import { useState, useRef, useCallback, useEffect } from "react"
import { ChevronDown, ChevronRight, Folder, FolderOpen, GripVertical } from "lucide-react"
import { Button } from "@/components/ui/button"
import { ScrollArea } from "@/components/ui/scroll-area"
import Link from "next/link"
import { usePathname } from "next/navigation"
import type { Category } from "@/lib/types"

interface SidebarProps {
  categories: Category[]
  selectedCategory?: string | null
  isOpen: boolean
  width: number
  onWidthChange: (width: number) => void
}

export default function Sidebar({ categories, selectedCategory, isOpen, width, onWidthChange }: SidebarProps) {
  const [expandedCategories, setExpandedCategories] = useState<Set<string>>(new Set())
  const [isResizing, setIsResizing] = useState(false)
  const sidebarRef = useRef<HTMLDivElement>(null)
  const pathname = usePathname()

  const toggleCategory = (categoryId: string) => {
    const newExpanded = new Set(expandedCategories)
    if (newExpanded.has(categoryId)) {
      newExpanded.delete(categoryId)
    } else {
      newExpanded.add(categoryId)
    }
    setExpandedCategories(newExpanded)
  }

  const isSelected = (categoryId: string) => {
    return pathname === `/blog/category/${categoryId}` || selectedCategory === categoryId
  }

  const handleMouseDown = useCallback((e: React.MouseEvent) => {
    setIsResizing(true)
    e.preventDefault()
  }, [])

  const handleMouseMove = useCallback(
    (e: MouseEvent) => {
      if (!isResizing) return

      const newWidth = e.clientX
      const minWidth = 200
      const maxWidth = 500

      if (newWidth >= minWidth && newWidth <= maxWidth) {
        onWidthChange(newWidth)
      }
    },
    [isResizing, onWidthChange],
  )

  const handleMouseUp = useCallback(() => {
    setIsResizing(false)
  }, [])

  // 마우스 이벤트 리스너 등록/해제
  // useState(() => {
  //   if (isResizing) {
  //     document.addEventListener("mousemove", handleMouseMove)
  //     document.addEventListener("mouseup", handleMouseUp)
  //   } else {
  //     document.removeEventListener("mousemove", handleMouseMove)
  //     document.removeEventListener("mouseup", handleMouseUp)
  //   }
  //
  //   return () => {
  //     document.removeEventListener("mousemove", handleMouseMove)
  //     document.removeEventListener("mouseup", handleMouseUp)
  //   }
  // })
  useEffect(() => {
    if (!isResizing) return

    document.addEventListener("mousemove", handleMouseMove)
    document.addEventListener("mouseup", handleMouseUp)

    return () => {
      document.removeEventListener("mousemove", handleMouseMove)
      document.removeEventListener("mouseup", handleMouseUp)
    }
  }, [isResizing, handleMouseMove, handleMouseUp])

  const renderCategory = (category: Category, level = 0) => {
    const isExpanded = expandedCategories.has(category.id)
    const selected = isSelected(category.id)
    const hasChildren = category.children && category.children.length > 0

    return (
      <div key={category.id}>
        <div className="flex">
          {hasChildren && (
            <Button variant="ghost" size="sm" onClick={() => toggleCategory(category.id)} className="p-1 h-auto">
              {isExpanded ? <ChevronDown className="h-4 w-4" /> : <ChevronRight className="h-4 w-4" />}
            </Button>
          )}

          <Button
            variant={selected ? "secondary" : "ghost"}
            className={`flex-1 justify-start gap-2 h-auto py-2 px-2 ${level > 0 ? `ml-${level * 4}` : ""} ${
              !hasChildren ? "ml-1" : ""
            }`}
            asChild
          >
            <Link href={`/blog/category/${category.id}`}>
              {hasChildren ? (
                isExpanded ? (
                  <FolderOpen className="h-4 w-4" />
                ) : (
                  <Folder className="h-4 w-4" />
                )
              ) : (
                <div className="w-4" />
              )}
              <span className="text-left truncate">{category.name}</span>
              <span className="ml-auto text-xs text-muted-foreground">{category.postCount}</span>
            </Link>
          </Button>
        </div>

        {hasChildren && isExpanded && (
          <div className="ml-4">{category.children?.map((child) => renderCategory(child, level + 1))}</div>
        )}
      </div>
    )
  }

  return (
    <>
      <aside
        ref={sidebarRef}
        className={`fixed left-0 top-16 h-[calc(100vh-4rem)] border-r bg-background transition-transform duration-300 z-40 ${
          isOpen ? "translate-x-0" : "-translate-x-full"
        }`}
        style={{ width: `${width}px` }}
      >
        <div className="p-4">
          <Button
            variant={pathname === "/blog" || pathname === "/" ? "secondary" : "ghost"}
            className="w-full justify-start mb-2"
            asChild
          >
            <Link href="/blog">전체 글</Link>
          </Button>
        </div>

        <ScrollArea className="h-[calc(100%-8rem)] px-4">
          <div className="space-y-1">{categories.map((category) => renderCategory(category))}</div>
        </ScrollArea>

        {/* 크기 조절 핸들 */}
        <div
          className={`absolute top-0 right-0 w-1 h-full cursor-col-resize hover:bg-primary/20 transition-colors ${
            isResizing ? "bg-primary/30" : ""
          }`}
          onMouseDown={handleMouseDown}
        >
          {/*<div className="absolute top-1/2 right-0 transform -translate-y-1/2 translate-x-1/2">*/}
          {/*  <GripVertical className="h-4 w-4 text-muted-foreground" />*/}
          {/*</div>*/}
        </div>
      </aside>

      {/* 크기 조절 중일 때 오버레이 */}
      {isResizing && <div className="fixed inset-0 z-50 cursor-col-resize" style={{ userSelect: "none" }} />}
    </>
  )
}
