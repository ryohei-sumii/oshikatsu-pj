/**
 * 推しメンバーバリデーター
 * Application層：入力値の検証ルール
 */

import { z } from 'zod';

export const oshiMemberFormSchema = z.object({
  groupId: z.number().min(1, 'グループを選択してください'),
  memberName: z
    .string()
    .min(1, 'メンバー名を入力してください'),
  memberNameKana: z
    .string()
    .min(1, 'メンバー名（カナ）を入力してください'),
  gender: z.number().min(0).max(1),
  birthDay: z
    .string()
    .min(1, '誕生日を入力してください')
    .regex(/^\d{4}-\d{2}-\d{2}$/, '誕生日は YYYY-MM-DD 形式で入力してください'),
});

export type OshiMemberFormValues = z.infer<typeof oshiMemberFormSchema>;
