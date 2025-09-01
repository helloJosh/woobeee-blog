import { Loader2 } from "lucide-react"


export default function Loading() {
  return (
      <div className="flex justify-center items-center h-[50vh]">
        <Loader2 className="animate-spin h-6 w-6 text-gray-500" />
      </div>
  )
}
