"use client"

import { useState } from "react"
import { Settings, ToggleLeft, ToggleRight } from "lucide-react"
import { Button } from "@/components/ui/button"
import { Card, CardContent, CardHeader } from "@/components/ui/card"
import { Badge } from "@/components/ui/badge"
import { Slider } from "@/components/ui/slider"
import { Popover, PopoverContent, PopoverTrigger } from "@/components/ui/popover"

interface InfiniteScrollSettingsProps {
    enableInfiniteScroll: boolean
    onToggleInfiniteScroll: (enabled: boolean) => void
    itemsPerPage: number
    onItemsPerPageChange: (count: number) => void
}

export default function InfiniteScrollSettings({
                                                   enableInfiniteScroll,
                                                   onToggleInfiniteScroll,
                                                   itemsPerPage,
                                                   onItemsPerPageChange,
                                               }: InfiniteScrollSettingsProps) {
    const [isOpen, setIsOpen] = useState(false)

    return (
        <Popover open={isOpen} onOpenChange={setIsOpen}>
            <PopoverTrigger asChild>
                <Button variant="outline" size="sm" className="gap-2 bg-transparent">
                    <Settings className="h-4 w-4" />
                    <span className="hidden sm:inline">표시 설정</span>
                </Button>
            </PopoverTrigger>
            <PopoverContent className="w-80" align="end">
                <Card className="border-0 shadow-none">
                    <CardHeader className="pb-3">
                        <h3 className="font-semibold">글 목록 표시 설정</h3>
                    </CardHeader>
                    <CardContent className="space-y-4">
                        {/* 무한 스크롤 토글 */}
                        <div className="flex items-center justify-between">
                            <div className="space-y-1">
                                <p className="text-sm font-medium">무한 스크롤</p>
                                <p className="text-xs text-muted-foreground">스크롤하면 자동으로 더 많은 글을 불러옵니다</p>
                            </div>
                            <Button
                                variant="ghost"
                                size="sm"
                                onClick={() => onToggleInfiniteScroll(!enableInfiniteScroll)}
                                className="p-1"
                            >
                                {enableInfiniteScroll ? (
                                    <ToggleRight className="h-6 w-6 text-primary" />
                                ) : (
                                    <ToggleLeft className="h-6 w-6 text-muted-foreground" />
                                )}
                            </Button>
                        </div>

                        {/* 페이지당 아이템 수 설정 */}
                        {enableInfiniteScroll && (
                            <div className="space-y-3">
                                <div className="flex items-center justify-between">
                                    <p className="text-sm font-medium">한 번에 로드할 글 수</p>
                                    <Badge variant="secondary">{itemsPerPage}개</Badge>
                                </div>
                                <Slider
                                    value={[itemsPerPage]}
                                    onValueChange={(value) => onItemsPerPageChange(value[0])}
                                    max={20}
                                    min={3}
                                    step={1}
                                    className="w-full"
                                />
                                <div className="flex justify-between text-xs text-muted-foreground">
                                    <span>3개</span>
                                    <span>20개</span>
                                </div>
                            </div>
                        )}

                        <div className="pt-2 border-t">
                            <p className="text-xs text-muted-foreground">
                                {enableInfiniteScroll
                                    ? "스크롤하거나 '더 보기' 버튼을 클릭해서 추가 글을 불러올 수 있습니다."
                                    : "모든 글이 한 번에 표시됩니다."}
                            </p>
                        </div>
                    </CardContent>
                </Card>
            </PopoverContent>
        </Popover>
    )
}
