/**
 * 推しグループバリデーター
 * Application層：入力値の検証ルール
 */

import { z } from 'zod';

export const oshiGroupFormSchema = z.object({
  groupName: z
    .string()
    .min(1, 'グループ名を入力してください'),
  company: z.string().optional(),
  description: z
    .string()
    .max(1000, '説明は1000文字以内で入力してください')
    .optional()
    .or(z.literal('')),
});

export type OshiGroupFormValues = z.infer<typeof oshiGroupFormSchema>;
