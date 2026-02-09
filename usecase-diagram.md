graph LR
    User["ユーザー"]
    Admin["管理者"]
    subgraph システム
        UC1(("会員登録"))
        UC2(("ログイン"))
        UC3(("推しグループ登録申請"))
        UC4(("推しメン登録"))
        UC5(("出演情報登録"))
        UC6(("登録申請確認"))
        UC7(("推しグループ登録実行"))
    end
    User --> UC1
    User --> UC2
    User --> UC3
    User --> UC4
    User --> UC5
    Admin --> UC6
    Admin --> UC7