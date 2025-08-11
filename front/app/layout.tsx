import type React from "react"
import type { Metadata } from "next"
import { Inter } from "next/font/google"
import "./globals.css"
import { ThemeProvider } from "@/components/theme-provider"
import { AuthProvider } from "@/hooks/use-auth"

const inter = Inter({ subsets: ["latin"] })

export const metadata: Metadata = {
  title: "Blog Website",
  description: "A modern blog with categories and comments",
}

export default function RootLayout({
                                     children,
                                   }: {
  children: React.ReactNode
}) {
  return (
      <html lang="ko">
      <body className={inter.className}>
      <ThemeProvider attribute="class" defaultTheme="light" enableSystem>
        <AuthProvider>{children}</AuthProvider>
      </ThemeProvider>
      </body>
      </html>
  )
}
