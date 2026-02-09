/**
 * 認証バリデーター
 * Application層：入力値の検証ルール
 */

import { z } from 'zod';

export const loginSchema = z.object({
  username: z
    .string()
    .min(1, 'ユーザー名を入力してください'),
  password: z
    .string()
    .min(1, 'パスワードを入力してください'),
});

export type LoginFormValues = z.infer<typeof loginSchema>;

export const registerSchema = z.object({
  username: z
    .string()
    .min(3, 'ユーザー名は3文字以上で入力してください')
    .max(50, 'ユーザー名は50文字以内で入力してください'),
  email: z
    .string()
    .min(1, 'メールアドレスを入力してください')
    .email('有効なメールアドレスを入力してください'),
  password: z
    .string()
    .min(1, 'パスワードを入力してください'),
});

export type RegisterFormValues = z.infer<typeof registerSchema>;
