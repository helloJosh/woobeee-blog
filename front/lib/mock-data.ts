import type { Category, Post, Comment } from "./types"

export const mockCategories: Category[] = [
  {
    id: "tech",
    name: "기술",
    postCount: 15,
    children: [
      { id: "frontend", name: "프론트엔드", postCount: 8 },
      { id: "backend", name: "백엔드", postCount: 5 },
      { id: "mobile", name: "모바일", postCount: 2 },
    ],
  },
  {
    id: "life",
    name: "일상",
    postCount: 12,
    children: [
      { id: "diary", name: "일기", postCount: 7 },
      { id: "travel", name: "여행", postCount: 5 },
    ],
  },
  {
    id: "review",
    name: "리뷰",
    postCount: 8,
    children: [
      { id: "book", name: "도서", postCount: 4 },
      { id: "movie", name: "영화", postCount: 4 },
    ],
  },
]

const mockComments: Comment[] = [
  {
    id: "1",
    author: "익명1",
    content: "정말 유용한 글이네요! 감사합니다.",
    createdAt: new Date(2024, 0, 15, 14, 30),
    replies: [
      {
        id: "2",
        author: "익명2",
        content: "저도 동감합니다!",
        createdAt: new Date(2024, 0, 15, 15, 0),
        replies: [],
      },
    ],
  },
  {
    id: "3",
    author: "익명3",
    content: "다음 글도 기대하겠습니다.",
    createdAt: new Date(2024, 0, 16, 9, 0),
    replies: [],
  },
]

// 더 많은 샘플 데이터 생성 (무한 스크롤 테스트용)
const generateMorePosts = (): Post[] => {
  const basePosts = [
    {
      id: "1",
      title: "React 18의 새로운 기능들",
      content: `# React 18의 새로운 기능들

React 18이 출시되면서 많은 새로운 기능들이 추가되었습니다.

## 주요 기능들

### 1. Concurrent Features
- **Suspense**: 데이터 로딩 상태를 더 우아하게 처리
- **useTransition**: 긴급하지 않은 업데이트를 지연시켜 성능 향상

### 2. Automatic Batching
여러 상태 업데이트를 자동으로 배치 처리하여 리렌더링 횟수를 줄입니다.

\`\`\`javascript
// React 18에서는 자동으로 배치 처리됩니다
function handleClick() {
  setCount(c => c + 1);
  setFlag(f => !f);
  // 한 번만 리렌더링됩니다
}
\`\`\`

### 3. Strict Mode 개선
개발 모드에서 더 엄격한 검사를 통해 잠재적 문제를 미리 발견할 수 있습니다.

## 마무리
React 18은 성능과 개발자 경험을 크게 향상시킨 버전입니다. 점진적으로 도입해보시기 바랍니다.`,
      category: "프론트엔드",
      categoryId: "frontend",
      views: 1250,
      likes: 42,
      createdAt: new Date(2024, 0, 15, 10, 0),
      comments: mockComments,
    },
    {
      id: "2",
      title: "Node.js 성능 최적화 팁",
      content: `# Node.js 성능 최적화 팁

Node.js 애플리케이션의 성능을 향상시키는 방법들을 알아보겠습니다.

## 1. 비동기 처리 최적화

### Promise.all 활용
여러 비동기 작업을 병렬로 처리하여 성능을 향상시킬 수 있습니다.

\`\`\`javascript
// 순차 처리 (느림)
const user = await getUser(id);
const posts = await getPosts(id);
const comments = await getComments(id);

// 병렬 처리 (빠름)
const [user, posts, comments] = await Promise.all([
  getUser(id),
  getPosts(id),
  getComments(id)
]);
\`\`\`

## 2. 메모리 관리

### 메모리 누수 방지
- 이벤트 리스너 정리
- 타이머 정리
- 클로저 사용 주의

## 3. 캐싱 전략
Redis나 메모리 캐시를 활용하여 반복적인 연산을 줄입니다.

성능 최적화는 지속적인 모니터링과 개선이 필요합니다.`,
      category: "백엔드",
      categoryId: "backend",
      views: 890,
      likes: 28,
      createdAt: new Date(2024, 0, 14, 16, 30),
      comments: [],
    },
    {
      id: "3",
      title: "제주도 3박 4일 여행 후기",
      content: `# 제주도 3박 4일 여행 후기

오랜만에 제주도에 다녀왔습니다. 정말 좋은 추억을 만들고 왔어요!

## 1일차: 제주시 도착
- **공항**: 제주국제공항 도착
- **숙소**: 제주시내 호텔 체크인
- **저녁**: 흑돼지 맛집에서 저녁식사
> dfdfd


## 2일차: 동쪽 코스
- **성산일출봉**: 일출 보기 (정말 장관이었어요!)
- **우도**: 페리 타고 우도 관광
- **점심**: 우도 땅콩 아이스크림
- **저녁**: 해산물 요리

## 3일차: 서쪽 코스
- **한라산**: 등반 (힘들었지만 보람있었어요)
- **점심**: 산 정상에서 도시락
- **협재해수욕장**: 해변 산책
- **저녁**: 갈치조림

## 4일차: 마무리
- **쇼핑**: 기념품 구매
- **공항**: 아쉬운 마음으로 출발

제주도는 언제 가도 좋은 것 같아요. 다음에는 더 오래 머물고 싶네요!`,
      category: "여행",
      categoryId: "travel",
      views: 2100,
      likes: 67,
      createdAt: new Date(2024, 0, 13, 20, 15),
      comments: [
        {
          id: "4",
          author: "여행러버",
          content: "저도 제주도 가고 싶어지네요! 좋은 정보 감사합니다.",
          createdAt: new Date(2024, 0, 14, 8, 30),
          replies: [],
        },
      ],
    },
  ]

  // 추가 포스트 생성 (무한 스크롤 테스트용)
  const additionalPosts: Post[] = []
  const categories = ["frontend", "backend", "mobile", "diary", "travel", "book", "movie"]
  const categoryNames = ["프론트엔드", "백엔드", "모바일", "일기", "여행", "도서", "영화"]

  for (let i = 4; i <= 50; i++) {
    const categoryIndex = Math.floor(Math.random() * categories.length)
    const randomViews = Math.floor(Math.random() * 5000) + 100
    const randomLikes = Math.floor(Math.random() * 100) + 1
    const randomDate = new Date(2024, 0, Math.floor(Math.random() * 30) + 1, Math.floor(Math.random() * 24))

    additionalPosts.push({
      id: i.toString(),
      title: `샘플 글 제목 ${i} - ${categoryNames[categoryIndex]} 관련`,
      content: `# 샘플 글 ${i}

이것은 무한 스크롤 테스트를 위한 샘플 글입니다.

## 내용

Lorem ipsum dolor sit amet, consectetur adipiscing elit. Sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat.

### 세부 내용

Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.

## 마무리

이 글은 ${i}번째 샘플 글입니다.`,
      category: categoryNames[categoryIndex],
      categoryId: categories[categoryIndex],
      views: randomViews,
      likes: randomLikes,
      createdAt: randomDate,
      comments: [],
    })
  }

  return [...basePosts, ...additionalPosts]
}

export const mockPosts: Post[] = generateMorePosts()
