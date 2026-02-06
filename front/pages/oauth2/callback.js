// pages/oauth2/callback.js
import { useEffect } from "react";   // react - view 상태
import { useRouter } from "next/router";  // 이동
import { useDispatch } from "react-redux";  // store (치킨집) 상태 
import { loginSuccess } from "../../reducers/authReducer";  // 액션 
export default function OAuth2CallbackPage(){
    const router = useRouter();
    const dispatch = useDispatch();

    useEffect( ()=>{
        if(!router.isReady) return;
        //http://localhost:3000/oauth2/callback?accessToken=eyJhbGciOiJIUzI1NiJ9.eyJ
        const { accessToken } = router.query;
        if(accessToken){
            try{
                localStorage.setItem("accessToken" , accessToken);  //## 토큰저장
                fetchUser(accessToken); // 사용자정보를 요청
            }catch(err){
                console.error( "OAuth2 callback error:", err);
                router.push("/login");
            }    
        }
    }, [router.isReady , router.query]);
    
    const fetchUser = async(accessToken)=>{  //##
        try {
        const res = await fetch(`${process.env.NEXT_PUBLIC_API_BASE_URL}/auth/me`, { //boot 경로
            headers: { Authorization: `Bearer ${accessToken}` },
            credentials: "include",  // refreshToken 쿠키와 함께 전송
        });
        if (res.ok) { // auth/me 통과가 됐다면
            const user = await res.json();
            dispatch(loginSuccess({ user, accessToken }));  // login 성공
            router.push("/mypage"); 
        } else {
            router.push("/login");
        }
        } catch (err) {
        console.error("User fetch error:", err);
        router.push("/login");
        }
    };
    return <p>소셜 로그인 처리 중입니다....</p>;
}