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

export const mockPosts: Post[] = [
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
  {
    id: "4",
    title: "클린 코드 리뷰",
    content: `# 클린 코드 - 로버트 C. 마틴

개발자라면 한 번은 읽어봐야 할 필독서를 리뷰해보겠습니다.

## 책 소개
- **저자**: 로버트 C. 마틴 (Uncle Bob)
- **출간**: 2008년
- **페이지**: 464페이지

## 주요 내용

### 1. 의미 있는 이름
변수, 함수, 클래스의 이름을 명확하게 짓는 방법

### 2. 함수
- 작게 만들어라
- 한 가지만 해라
- 서술적인 이름을 사용해라

### 3. 주석
좋은 코드는 주석이 필요 없다. 코드 자체가 문서가 되어야 한다.

### 4. 형식 맞추기
일관된 코딩 스타일의 중요성

## 개인적인 생각
이 책을 읽고 나서 코드를 작성하는 방식이 완전히 바뀌었습니다. 
특히 함수를 작게 만들고 의미 있는 이름을 짓는 습관이 생겼어요.

## 평점: ⭐⭐⭐⭐⭐

모든 개발자에게 추천하는 책입니다!`,
    category: "도서",
    categoryId: "book",
    views: 756,
    likes: 34,
    createdAt: new Date(2024, 0, 12, 14, 0),
    comments: [],
  },
  {
    id: "5",
    title: "오늘의 일기 - 새로운 프로젝트 시작",
    content: `# 새로운 프로젝트 시작

오늘부터 새로운 프로젝트를 시작하게 되었다.

## 프로젝트 개요
- **기간**: 3개월
- **팀원**: 5명
- **기술스택**: React, Node.js, MongoDB

## 오늘 한 일
1. 프로젝트 킥오프 미팅
2. 요구사항 분석
3. 기술스택 논의
4. 일정 계획 수립

## 느낀 점
새로운 도전이 시작되어서 설레면서도 긴장된다. 
팀원들과 좋은 시너지를 내서 성공적인 프로젝트를 만들고 싶다.

## 내일 할 일
- [ ] 프로젝트 환경 설정
- [ ] 데이터베이스 설계
- [ ] UI/UX 디자인 검토

화이팅! 💪`,
    category: "일기",
    categoryId: "diary",
    views: 423,
    likes: 12,
    createdAt: new Date(2024, 0, 11, 22, 30),
    comments: [],
  },
]
