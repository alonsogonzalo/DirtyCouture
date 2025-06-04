import { createClient, SupabaseClient } from '@supabase/supabase-js'

const supabaseUrl: string = 'https://htoqpzqenuwxqhwfgzsy.supabase.co'
const supabaseKey: string = 'eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6Imh0b3FwenFlbnV3eHFod2ZnenN5Iiwicm9sZSI6ImFub24iLCJpYXQiOjE3NDE3MTUxMTQsImV4cCI6MjA1NzI5MTExNH0.eCeoVAsJbvfc4hwW31ZWH7AQ8uYYuqVp9IEEIOsOPdc'

export const supabase: SupabaseClient = createClient(supabaseUrl, supabaseKey)
