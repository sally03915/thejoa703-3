import React from "react";
import { Button, message } from "antd";
import { UserAddOutlined, UserDeleteOutlined } from "@ant-design/icons";

export default function FollowButton({
    authorId,
    user,
    isFollowing,
    onToggleFollow,
    loading,
}) {
  const handleClick = () => {
    if (!user) {
      message.info("로그인한 사용자만 팔로우할 수 있습니다.");
      window.location.href = "/login";
      return;
    }
    if (!authorId) {
      message.error("팔로우 대상 ID가 없습니다.");
      return;
    }
    if (authorId === user.id) {
      message.warning("자기 자신은 팔로우할 수 없습니다.");
      return;
    }
    if (loading) return;

    // if (isBlocked) {
    //   message.warning("차단을 해제해야 팔로우할 수 있습니다.");
    //   if (typeof onUnblock === "function") {
    //     onUnblock(authorId);
    //   }
    //   return;
    // }

    if (typeof onToggleFollow === "function") {
      onToggleFollow(authorId);
    }
  };

  return (
    <Button
      type={ isFollowing ? "default" : "primary"} //##
      danger={isFollowing}
      icon={isFollowing ? <UserDeleteOutlined /> : <UserAddOutlined />}  //##
      loading={loading}
      onClick={handleClick}
      style={{ borderRadius: "6px" }}
    //##   disabled={isBlocked}  
    >
      { isFollowing ? "언팔로우" : "팔로우"}
    </Button>
  );
}

