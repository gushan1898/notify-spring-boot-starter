#!/bin/bash

# 颜色定义
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# 打印带颜色的消息
print_message() {
    local color=$1
    local message=$2
    echo -e "${color}${message}${NC}"
}

# 检查命令执行结果
check_result() {
    if [ $? -eq 0 ]; then
        print_message "$GREEN" "✓ $1 成功"
    else
        print_message "$RED" "✗ $1 失败"
        exit 1
    fi
}

# 显示帮助信息
show_help() {
    echo "用法: $0 [选项]"
    echo "选项:"
    echo "  -h, --help     显示帮助信息"
    echo "  -v, --version  指定发布版本号"
    echo "  -l, --local    发布到本地仓库"
    echo "  -s, --snapshot 发布快照版本"
    echo "示例:"
    echo "  $0 -v 1.0.0    # 发布正式版本1.0.0"
    echo "  $0 -s          # 发布快照版本"
    echo "  $0 -l          # 发布到本地仓库"
}

# 默认值
LOCAL_DEPLOY=false
SNAPSHOT=false
VERSION=""

# 解析命令行参数
while [[ $# -gt 0 ]]; do
    case $1 in
        -h|--help)
            show_help
            exit 0
            ;;
        -v|--version)
            VERSION="$2"
            shift
            shift
            ;;
        -l|--local)
            LOCAL_DEPLOY=true
            shift
            ;;
        -s|--snapshot)
            SNAPSHOT=true
            shift
            ;;
        *)
            print_message "$RED" "未知选项: $1"
            show_help
            exit 1
            ;;
    esac
done

# 清理并编译项目
print_message "$YELLOW" "正在清理并编译项目..."
mvn clean compile
check_result "清理并编译"

# 运行测试
print_message "$YELLOW" "正在运行测试..."
mvn test
check_result "测试"

# 设置版本号
if [ ! -z "$VERSION" ]; then
    print_message "$YELLOW" "正在设置版本号为 $VERSION..."
    mvn versions:set -DnewVersion=$VERSION
    check_result "设置版本号"
fi

# 部署
if [ "$LOCAL_DEPLOY" = true ]; then
    # 发布到本地仓库
    print_message "$YELLOW" "正在发布到本地仓库..."
    mvn clean install
    check_result "本地仓库发布"
else
    # 发布到远程仓库
    if [ "$SNAPSHOT" = true ]; then
        print_message "$YELLOW" "正在发布快照版本..."
        mvn clean deploy
        check_result "快照版本发布"
    else
        print_message "$YELLOW" "正在发布正式版本..."
        mvn clean deploy -P release
        check_result "正式版本发布"
    fi
fi

print_message "$GREEN" "✨ 发布完成!" 